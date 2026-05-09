package io.github.kroune.pollen.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsLanguageViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val locale: StateFlow<AppLocale> = settingsRepository.locale
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLocale.RU)

    fun setLocale(locale: AppLocale) {
        viewModelScope.launch { settingsRepository.setLocale(locale) }
    }
}
