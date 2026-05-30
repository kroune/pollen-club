package io.github.kroune.pollen.presentation.settings.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.presentation.common.CollectEffects
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun SettingsLanguageScreen(
    state: SettingsLanguageUiState,
    effects: Flow<UiEvent> = emptyFlow(),
    onIntent: (SettingsLanguageIntent) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects(effects, snackbarHostState)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(MR.strings.back),
                    tint = PollenTheme.colors.ink2,
                    modifier = Modifier.size(22.dp),
                )
            }
            Text(
                text = stringResource(MR.strings.settings_language),
                style = MaterialTheme.typography.displaySmall,
                color = PollenTheme.colors.ink,
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column {
                LanguageRow(
                    label = stringResource(MR.strings.language_russian),
                    isSelected = state.locale == AppLocale.RU,
                    onClick = { onIntent(SettingsLanguageIntent.SetLocale(AppLocale.RU)) },
                )
                HorizontalDivider(color = PollenTheme.colors.line2)
                LanguageRow(
                    label = stringResource(MR.strings.language_english),
                    isSelected = state.locale == AppLocale.EN,
                    onClick = { onIntent(SettingsLanguageIntent.SetLocale(AppLocale.EN)) },
                )
            }
        }
    }
    }
}

// region Previews

@Preview
@Composable
private fun PreviewSettingsLanguageRu() {
    PollenTheme {
        SettingsLanguageScreen(state = SettingsLanguageUiState(locale = AppLocale.RU))
    }
}

@Preview
@Composable
private fun PreviewSettingsLanguageEn() {
    PollenTheme {
        SettingsLanguageScreen(state = SettingsLanguageUiState(locale = AppLocale.EN))
    }
}

// endregion

@Composable
private fun LanguageRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.ink,
            modifier = Modifier.weight(1f),
        )
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = PollenTheme.colors.accent,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
