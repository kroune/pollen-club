package io.github.kroune.pollen.presentation.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SettingsRepository
import io.github.kroune.pollen.domain.repository.UserRepository
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@Stable
data class SettingsUiState(
    val data: LoadState<SettingsData> = LoadState.Loading,
)

@Stable
data class SettingsData(
    val participantCode: String?,
    val languageLabel: StringDesc,
    val regionLabel: String?,
    val mainAllergenLabel: String?,
    val friendsLabel: StringDesc?,
)

sealed interface SettingsIntent {
    data object LoadData : SettingsIntent
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val pollenRepository: PollenRepository,
    private val friendsRepository: FriendsRepository,
) : MviViewModel<SettingsUiState, SettingsIntent, UiEvent>(SettingsUiState()) {

    private var loadJob: Job? = null

    init {
        onIntent(SettingsIntent.LoadData)
    }

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        loadJob?.cancel()
        updateState { SettingsUiState(data = LoadState.Loading) }
        loadJob = viewModelScope.launch {
            combine(
                userRepository.observeUser(),
                settingsRepository.locale,
                locationRepository.observeLocations(),
                pollenRepository.observePollens(),
                friendsRepository.observeFriends(),
            ) { user, locale, locations, pollens, friends ->
                val participantCode = user?.serverId?.takeIf { it > 0 }?.toString()

                val languageLabel: StringDesc = when (locale) {
                    AppLocale.RU -> MR.strings.language_russian.desc()
                    AppLocale.EN -> MR.strings.language_english.desc()
                }

                val regionLabel = if (user != null && user.location > 0) {
                    locations.firstOrNull { it.id == user.location }?.name
                } else {
                    null
                }

                val mainAllergenLabel = if (user != null && user.selectedAllergens.isNotEmpty()) {
                    val primaryId = user.selectedAllergens.first()
                    pollens.firstOrNull { it.id == primaryId }?.name
                } else {
                    null
                }

                val friendsCount = friends.size
                val friendsLabel: StringDesc? = if (friendsCount > 0) {
                    StringDesc.ResourceFormatted(MR.strings.settings_friends_count, friendsCount)
                } else {
                    null
                }

                SettingsData(
                    participantCode = participantCode,
                    languageLabel = languageLabel,
                    regionLabel = regionLabel,
                    mainAllergenLabel = mainAllergenLabel,
                    friendsLabel = friendsLabel,
                )
            }.collect { data ->
                updateState { SettingsUiState(data = LoadState.Loaded(data)) }
            }
        }
    }
}
