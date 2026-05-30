package io.github.kroune.pollen.presentation.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.primaryPollenId
import io.github.kroune.pollen.domain.model.serverIdOrNull
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.domain.repository.SettingsRepository
import io.github.kroune.pollen.domain.session.UserSession
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
    private val userSession: UserSession,
    private val locationRepository: LocationRepository,
    private val pollenRepository: PollenRepository,
    private val sensitivityRepository: SensitivityRepository,
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
        // The user's primary allergen is their most-sensitive pollen (from the sensitivity table).
        val mainAllergenLabel = combine(
            pollenRepository.observePollens(),
            sensitivityRepository.observeAll(),
        ) { pollens, sensitivities ->
            sensitivities.primaryPollenId()
                ?.let { pollenId -> pollens.firstOrNull { it.id == pollenId }?.name }
        }

        loadJob = viewModelScope.launch {
            combine(
                userSession.user,
                settingsRepository.locale,
                locationRepository.observeLocations(),
                mainAllergenLabel,
                friendsRepository.observeFriends(),
            ) { user, locale, locations, allergenLabel, friends ->
                val participantCode = user.identity.serverIdOrNull?.toString()

                val languageLabel: StringDesc = when (locale) {
                    AppLocale.RU -> MR.strings.language_russian.desc()
                    AppLocale.EN -> MR.strings.language_english.desc()
                }

                val regionLabel = user.location?.let { locationId ->
                    locations.firstOrNull { it.id == locationId }?.name
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
                    mainAllergenLabel = allergenLabel,
                    friendsLabel = friendsLabel,
                )
            }.collect { data ->
                updateState { SettingsUiState(data = LoadState.Loaded(data)) }
            }
        }
    }
}
