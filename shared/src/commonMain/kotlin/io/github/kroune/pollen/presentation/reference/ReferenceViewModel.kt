package io.github.kroune.pollen.presentation.reference

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.home.PollenIconRegistry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import kotlin.coroutines.cancellation.CancellationException

data class ReferenceUiState(
    val allergens: LoadState<ImmutableList<ReferenceAllergenUi>> = LoadState.Loading,
    val searchQuery: String = "",
)

@Immutable
data class ReferenceAllergenUi(
    val pollenId: Int,
    val name: String,
    val nameEng: String,
    val description: String,
    val iconRes: DrawableResource?,
    val severityLevel: Int,
    val severityLabel: String,
)

class ReferenceViewModel(
    private val pollenRepository: PollenRepository,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ReferenceUiState())
    val state: StateFlow<ReferenceUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        _state.value = ReferenceUiState(searchQuery = _state.value.searchQuery)

        viewModelScope.launch {
            try {
                val pollens = pollenRepository.observePollens().first()

                val user = userRepository.getLocalUser()
                val locationId = if (user != null && user.location > 0) {
                    user.location
                } else {
                    locationRepository.getAll().firstOrNull()?.id
                }

                val levelMap = if (locationId != null) {
                    val today = kotlin.time.Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                    val live = pollenRepository.getLevelsForLocation(locationId, today)
                    val levels = live.ifEmpty {
                        pollenRepository.getForecastsForLocation(locationId, today)
                    }
                    levels.associateBy { it.pollenId }
                } else {
                    emptyMap()
                }

                val allergens = pollens.map { pollen ->
                    val currentLevel = levelMap[pollen.id]?.value ?: 0
                    val label = if (currentLevel > 0) {
                        pollen.levels.firstOrNull { it.level == currentLevel }?.name ?: "не активен"
                    } else {
                        "не активен"
                    }
                    ReferenceAllergenUi(
                        pollenId = pollen.id,
                        name = pollen.name,
                        nameEng = pollen.nameEng,
                        description = pollen.description,
                        iconRes = PollenIconRegistry.iconFor(pollen),
                        severityLevel = currentLevel,
                        severityLabel = label,
                    )
                }.toImmutableList()

                _state.value = _state.value.copy(allergens = LoadState.Loaded(allergens))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(allergens = LoadState.Failed)
                _events.send(UiEvent.ShowError("Не удалось загрузить справочник"))
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }
}
