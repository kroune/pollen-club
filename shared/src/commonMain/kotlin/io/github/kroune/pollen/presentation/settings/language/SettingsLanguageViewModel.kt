package io.github.kroune.pollen.presentation.settings.language

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.repository.SettingsRepository
import io.github.kroune.pollen.presentation.common.MviViewModel
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.util.runCatchingCancellable
import kotlinx.coroutines.launch

@Stable
data class SettingsLanguageUiState(
    val locale: AppLocale = AppLocale.RU,
)

sealed interface SettingsLanguageIntent {
    data class SetLocale(val locale: AppLocale) : SettingsLanguageIntent
}

class SettingsLanguageViewModel(
    private val settingsRepository: SettingsRepository,
) : MviViewModel<SettingsLanguageUiState, SettingsLanguageIntent, UiEvent>(SettingsLanguageUiState()) {

    init {
        viewModelScope.launch {
            settingsRepository.locale.collect { locale ->
                updateState { copy(locale = locale) }
            }
        }
    }

    override fun handleIntent(intent: SettingsLanguageIntent) {
        when (intent) {
            is SettingsLanguageIntent.SetLocale -> {
                viewModelScope.launch {
                    runCatchingCancellable {
                        settingsRepository.setLocale(intent.locale)
                    }.onFailure {
                        emitEffect(UiEvent.ShowError(MR.strings.error_change_language.desc()))
                    }
                }
            }
        }
    }
}
