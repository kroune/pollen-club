package io.github.kroune.pollen.presentation.phenology

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.KnownPollens
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.PhenologyObservationDomain
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.model.UserDomain
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PhenologyRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.domain.usecase.CoordinateResolver
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Stable
data class PhenologyFormState(
    val selectedStage: Int = 1,
    val comment: String = "",
    val showDialog: Boolean = false,
)

@Stable
data class PhenologyUiState(
    val screenData: LoadState<PhenologyScreenDataUi> = LoadState.Loading,
    val form: PhenologyFormState = PhenologyFormState(),
    val today: LocalDate? = null,
)

sealed interface PhenologyIntent {
    data object ShowAddDialog : PhenologyIntent
    data object DismissDialog : PhenologyIntent
    data class SelectStage(val stageNumber: Int) : PhenologyIntent
    data class SetComment(val comment: String) : PhenologyIntent
    data object Submit : PhenologyIntent
}

class PhenologyViewModel(
    private val phenologyRepository: PhenologyRepository,
    private val userRepository: UserRepository,
    private val localeProvider: LocaleProvider,
    private val pollenRepository: PollenRepository,
    private val coordinateResolver: CoordinateResolver,
    private val locationRepository: LocationRepository,
    private val todayProvider: TodayProvider,
) : MviViewModel<PhenologyUiState, PhenologyIntent, UiEvent>(PhenologyUiState()) {

    init {
        observeStages()
        observeAllergenName()
        observeLocationLabel()
        observeToday()
    }

    private fun observeToday() {
        viewModelScope.launch {
            todayProvider.today.collect { today ->
                updateState { copy(today = today) }
            }
        }
    }

    override fun handleIntent(intent: PhenologyIntent) {
        when (intent) {
            PhenologyIntent.ShowAddDialog -> showAddDialog()
            PhenologyIntent.DismissDialog -> updateState { copy(form = PhenologyFormState()) }
            is PhenologyIntent.SelectStage -> updateState { copy(form = form.copy(selectedStage = intent.stageNumber)) }
            is PhenologyIntent.SetComment -> updateState { copy(form = form.copy(comment = intent.comment)) }
            PhenologyIntent.Submit -> submit()
        }
    }

    private fun observeStages() {
        viewModelScope.launch {
            combine(
                phenologyRepository.observeObservations(),
                localeProvider.currentLocale,
            ) { observations, locale ->
                val stages = mapPhenologyStages(observations, locale)
                val currentStage = stages.firstOrNull { it.isCurrent }
                val lastObservation = observations.maxByOrNull { it.time }
                StagesSlice(
                    stages = stages,
                    currentStageNumber = currentStage?.number,
                    currentStageName = currentStage?.name.orEmpty(),
                    currentStageEpochSeconds = lastObservation?.time,
                )
            }.collect { slice ->
                updateScreenData {
                    copy(
                        stages = slice.stages.toImmutableList(),
                        currentStageNumber = slice.currentStageNumber,
                        currentStageName = slice.currentStageName,
                        currentStageEpochSeconds = slice.currentStageEpochSeconds,
                    )
                }
            }
        }
    }

    private fun observeAllergenName() {
        viewModelScope.launch {
            pollenRepository.observePollens().collect { pollens ->
                val name = pollens.firstOrNull { it.id == KnownPollens.BIRCH_POLLEN_ID }?.name ?: ""
                updateScreenData { copy(allergenName = name) }
            }
        }
    }

    private fun observeLocationLabel() {
        viewModelScope.launch {
            combine(
                userRepository.observeUser(),
                locationRepository.observeLocations(),
                ::resolveLocationName,
            ).collect { label ->
                updateScreenData { copy(locationLabel = label) }
            }
        }
    }

    private fun resolveLocationName(user: UserDomain?, locations: List<LocationDomain>): String {
        val locationId = user?.location?.takeIf { it > 0 } ?: return ""
        return locations.firstOrNull { it.id == locationId }?.name ?: ""
    }

    private inline fun updateScreenData(crossinline transform: PhenologyScreenDataUi.() -> PhenologyScreenDataUi) {
        updateState {
            val current = screenData.dataOrNull ?: EmptyScreenData
            copy(screenData = LoadState.Loaded(current.transform()))
        }
    }

    private fun showAddDialog() {
        val current = currentState.screenData
        val nextStage = if (current is LoadState.Loaded) {
            val currentNum = current.data.stages.firstOrNull { it.isCurrent }?.number ?: 0
            (currentNum + 1).coerceIn(STAGE_MIN, STAGE_MAX)
        } else {
            STAGE_MIN
        }
        updateState { copy(form = PhenologyFormState(selectedStage = nextStage, showDialog = true)) }
    }

    private fun submit() {
        viewModelScope.launch {
            runCatchingCancellable {
                val now = kotlin.time.Clock.System.now()
                val local = now.toLocalDateTime(TimeZone.currentSystemDefault())
                val user = userRepository.getLocalUser()
                val location = coordinateResolver.resolve()
                val form = currentState.form
                phenologyRepository.submitObservation(
                    PhenologyObservationDomain(
                        date = local.date,
                        time = now.epochSeconds,
                        state = form.selectedStage - STAGE_MIN,
                        latitude = location?.latitude ?: 0.0,
                        longitude = location?.longitude ?: 0.0,
                        comment = form.comment,
                    ),
                    userId = user?.serverId ?: 0,
                )
                updateState { copy(form = PhenologyFormState()) }
            }.onFailure {
                updateState { copy(form = form.copy(showDialog = false)) }
                emitEffect(UiEvent.ShowError(MR.strings.error_submit_observation.desc()))
            }
        }
    }

    private data class StagesSlice(
        val stages: List<PhenologyStageUi>,
        val currentStageNumber: Int?,
        val currentStageName: String,
        val currentStageEpochSeconds: Long?,
    )

    companion object {
        private const val STAGE_MIN = 1
        private const val STAGE_MAX = 6

        private val EmptyScreenData = PhenologyScreenDataUi(
            allergenName = "",
            locationLabel = "",
            currentStageNumber = null,
            currentStageName = "",
            currentStageEpochSeconds = null,
            stages = persistentListOf(),
        )
    }
}
