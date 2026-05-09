package io.github.kroune.pollen.presentation.phenology

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.PhenologyObservationDomain
import io.github.kroune.pollen.domain.repository.PhenologyRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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
) : ViewModel() {

    private val _state = MutableStateFlow(PhenologyUiState())
    val state: StateFlow<PhenologyUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(screenData = LoadState.Loading)
                val locale = localeProvider.currentLocale.first()
                // TODO: replace mock observations with phenologyRepository.observeObservations()
                val mockObservations = buildMockObservations()
                val screenData = buildScreenData(mockObservations, locale)
                _state.value = _state.value.copy(screenData = LoadState.Loaded(screenData))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(screenData = LoadState.Failed)
                _events.send(UiEvent.ShowError("Не удалось загрузить данные фенологии"))
            }
        }
    }

    private fun buildMockObservations(): List<PhenologyObservationDomain> {
        // TODO: replace with real observations from repository
        val now = kotlin.time.Clock.System.now()
        val threeDaysAgo = now.minus(kotlin.time.Duration.parse("3d"))
        return listOf(
            PhenologyObservationDomain(
                id = 1,
                date = threeDaysAgo.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
                time = threeDaysAgo.epochSeconds,
                state = 0,
                latitude = 55.75,
                longitude = 37.62,
                comment = "",
            ),
            PhenologyObservationDomain(
                id = 2,
                date = threeDaysAgo.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
                time = threeDaysAgo.epochSeconds,
                state = 1,
                latitude = 55.75,
                longitude = 37.62,
                comment = "",
            ),
            PhenologyObservationDomain(
                id = 3,
                date = threeDaysAgo.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
                time = threeDaysAgo.epochSeconds,
                state = 2,
                latitude = 55.75,
                longitude = 37.62,
                comment = "",
            ),
        )
    }

    private fun buildScreenData(
        observations: List<PhenologyObservationDomain>,
        locale: AppLocale,
    ): PhenologyScreenDataUi {
        val stages = mapPhenologyStages(observations, locale)
        val currentStage = stages.firstOrNull { it.isCurrent }
        val lastObservation = observations.maxByOrNull { it.time }

        // TODO: get actual allergen name from user's primary allergen setting
        val allergenName = if (locale == AppLocale.RU) "Берёза" else "Birch"
        // TODO: get actual location from user's selected monitoring region
        val locationLabel = if (locale == AppLocale.RU) "Москва, Шереметьево · фенология" else "Moscow, Sheremetyevo · phenology"

        return PhenologyScreenDataUi(
            allergenName = allergenName,
            locationLabel = locationLabel,
            currentStageLabel = currentStage?.let { "№${it.number} · ${it.name}" } ?: "",
            currentStageDate = lastObservation?.let { formatObservationDate(it.time, locale) } ?: "",
            stages = stages,
        )
    }

    fun showAddDialog() {
        val current = _state.value.screenData
        val nextStage = if (current is LoadState.Loaded) {
            val currentNum = current.data.stages.firstOrNull { it.isCurrent }?.number ?: 0
            (currentNum + 1).coerceIn(1, 6)
        } else {
            1
        }
        _state.value = _state.value.copy(
            form = PhenologyFormState(selectedStage = nextStage, showDialog = true),
        )
    }

    fun dismissDialog() {
        _state.value = _state.value.copy(form = PhenologyFormState())
    }

    fun selectStage(stageNumber: Int) {
        _state.value = _state.value.copy(
            form = _state.value.form.copy(selectedStage = stageNumber),
        )
    }

    fun setComment(comment: String) {
        _state.value = _state.value.copy(
            form = _state.value.form.copy(comment = comment),
        )
    }

    fun submit() {
        viewModelScope.launch {
            try {
                val now = kotlin.time.Clock.System.now()
                val local = now.toLocalDateTime(TimeZone.currentSystemDefault())
                val user = userRepository.getLocalUser()
                phenologyRepository.submitObservation(
                    PhenologyObservationDomain(
                        date = local.date.toString(),
                        time = now.epochSeconds,
                        state = _state.value.form.selectedStage - 1,
                        // TODO: use actual GPS coordinates
                        latitude = 0.0,
                        longitude = 0.0,
                        comment = _state.value.form.comment,
                    ),
                    userId = user?.serverId ?: 0,
                )
                dismissDialog()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(form = _state.value.form.copy(showDialog = false))
                _events.send(UiEvent.ShowError("Не удалось отправить наблюдение"))
            }
        }
    }
}

private val MONTHS_RU = arrayOf(
    "янв", "фев", "мар", "апр", "мая", "июн",
    "июл", "авг", "сен", "окт", "ноя", "дек",
)

private val MONTHS_EN = arrayOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)

private fun formatObservationDate(epochSeconds: Long, locale: AppLocale): String {
    val now = kotlin.time.Clock.System.now()
    val diffSeconds = now.epochSeconds - epochSeconds
    val diffDays = abs(diffSeconds / 86400)

    val instant = kotlin.time.Instant.fromEpochSeconds(epochSeconds)
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val months = if (locale == AppLocale.RU) MONTHS_RU else MONTHS_EN
    val dateStr = "${local.dayOfMonth} ${months[local.monthNumber - 1]}"

    val relativeStr = when {
        diffDays == 0L -> if (locale == AppLocale.RU) "сегодня" else "today"
        diffDays == 1L -> if (locale == AppLocale.RU) "вчера" else "yesterday"
        diffDays < 7L -> if (locale == AppLocale.RU) "$diffDays дн. назад" else "$diffDays days ago"
        else -> null
    }

    return if (relativeStr != null) "$dateStr · $relativeStr" else dateStr
}
