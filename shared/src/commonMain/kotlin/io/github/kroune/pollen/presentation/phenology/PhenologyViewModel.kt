package io.github.kroune.pollen.presentation.phenology

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.AppLocale
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.PhenologyObservationDomain
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.UserDomain
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PhenologyRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.domain.usecase.CoordinateResolver
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
)

class PhenologyViewModel(
    private val phenologyRepository: PhenologyRepository,
    private val userRepository: UserRepository,
    private val localeProvider: LocaleProvider,
    private val pollenRepository: PollenRepository,
    private val coordinateResolver: CoordinateResolver,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _form = MutableStateFlow(PhenologyFormState())
    private val _refreshTrigger = MutableStateFlow(0)

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<PhenologyUiState> = _refreshTrigger.flatMapLatest {
        combine(
            combine(
                phenologyRepository.observeObservations(),
                userRepository.observeUser(),
                pollenRepository.observePollens(),
                localeProvider.currentLocale,
                locationRepository.observeLocations(),
            ) { observations, user, pollens, locale, locations ->
                buildScreenData(observations, user, pollens, locale, locations)
            },
            _form,
        ) { screenData, form ->
            PhenologyUiState(screenData = LoadState.Loaded(screenData), form = form)
        }.catch {
            emit(PhenologyUiState(screenData = LoadState.Failed))
            _events.send(UiEvent.ShowError(MR.strings.error_load_phenology.desc()))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PhenologyUiState())

    fun loadData() {
        _refreshTrigger.value++
    }

    fun showAddDialog() {
        val current = state.value.screenData
        val nextStage = if (current is LoadState.Loaded) {
            val currentNum = current.data.stages.firstOrNull { it.isCurrent }?.number ?: 0
            (currentNum + 1).coerceIn(1, 6)
        } else {
            1
        }
        _form.value = PhenologyFormState(selectedStage = nextStage, showDialog = true)
    }

    fun dismissDialog() {
        _form.value = PhenologyFormState()
    }

    fun selectStage(stageNumber: Int) {
        _form.value = _form.value.copy(selectedStage = stageNumber)
    }

    fun setComment(comment: String) {
        _form.value = _form.value.copy(comment = comment)
    }

    fun submit() {
        viewModelScope.launch {
            try {
                val now = kotlin.time.Clock.System.now()
                val local = now.toLocalDateTime(TimeZone.currentSystemDefault())
                val user = userRepository.getLocalUser()
                val location = coordinateResolver.resolve()
                phenologyRepository.submitObservation(
                    PhenologyObservationDomain(
                        date = local.date.toString(),
                        time = now.epochSeconds,
                        state = _form.value.selectedStage - 1,
                        latitude = location?.latitude ?: 0.0,
                        longitude = location?.longitude ?: 0.0,
                        comment = _form.value.comment,
                    ),
                    userId = user?.serverId ?: 0,
                )
                dismissDialog()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _form.value = _form.value.copy(showDialog = false)
                _events.send(UiEvent.ShowError(MR.strings.error_submit_observation.desc()))
            }
        }
    }

    private fun buildScreenData(
        observations: List<PhenologyObservationDomain>,
        user: UserDomain?,
        pollens: List<PollenDomain>,
        locale: AppLocale,
        locations: List<LocationDomain>,
    ): PhenologyScreenDataUi {
        val stages = mapPhenologyStages(observations, locale)
        val currentStage = stages.firstOrNull { it.isCurrent }
        val lastObservation = observations.maxByOrNull { it.time }

        val allergenName = pollens.firstOrNull { it.id == BIRCH_POLLEN_ID }?.name ?: ""

        val locationLabel = user?.location?.takeIf { it > 0 }
            ?.let { locId -> locations.firstOrNull { it.id == locId }?.name }
            ?: ""

        return PhenologyScreenDataUi(
            allergenName = allergenName,
            locationLabel = locationLabel,
            currentStageLabel = currentStage?.let { "№${it.number} · ${it.name}" } ?: "",
            currentStageEpochSeconds = lastObservation?.time,
            stages = stages,
        )
    }

    companion object {
        private const val BIRCH_POLLEN_ID = 1
    }
}

