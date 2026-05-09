package io.github.kroune.pollen.presentation.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SettingsRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Stable
data class SettingsUiState(
    val data: LoadState<SettingsData> = LoadState.Loading,
)

@Stable
data class SettingsData(
    val participantCode: String,
    val languageLabel: String,
    val regionLabel: String,
    val mainAllergenLabel: String,
    val friendsLabel: String,
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val pollenRepository: PollenRepository,
    private val friendsRepository: FriendsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = SettingsUiState(data = LoadState.Loading)
            try {
                combine(
                    userRepository.observeUser(),
                    settingsRepository.locale,
                    locationRepository.observeLocations(),
                    pollenRepository.observePollens(),
                    friendsRepository.observeFriends(),
                ) { user, locale, locations, pollens, friends ->
                    val participantCode = user?.serverId?.takeIf { it > 0 }?.toString() ?: "—"

                    val languageLabel = when (locale) {
                        AppLocale.RU -> "Русский"
                        AppLocale.EN -> "English"
                    }

                    val regionLabel = if (user != null && user.location > 0) {
                        locations.firstOrNull { it.id == user.location }?.name ?: "—"
                    } else {
                        "—"
                    }

                    val mainAllergenLabel = if (user != null && user.selectedAllergens.isNotEmpty()) {
                        val primaryId = user.selectedAllergens.first()
                        pollens.firstOrNull { it.id == primaryId }?.name ?: "—"
                    } else {
                        "—"
                    }

                    val friendsCount = friends.size
                    val friendsLabel = if (friendsCount > 0) {
                        "$friendsCount участн."
                    } else {
                        "—"
                    }

                    SettingsData(
                        participantCode = participantCode,
                        languageLabel = languageLabel,
                        regionLabel = regionLabel,
                        mainAllergenLabel = mainAllergenLabel,
                        friendsLabel = friendsLabel,
                    )
                }.collect { data ->
                    _state.value = SettingsUiState(data = LoadState.Loaded(data))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = SettingsUiState(data = LoadState.Failed)
                _events.send(UiEvent.ShowError("Не удалось загрузить настройки"))
            }
        }
    }
}
