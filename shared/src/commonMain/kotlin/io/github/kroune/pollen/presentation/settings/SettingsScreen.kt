package io.github.kroune.pollen.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.common.FullScreenError
import androidx.compose.ui.tooling.preview.Preview
import io.github.kroune.pollen.presentation.common.shimmerEffect
import io.github.kroune.pollen.presentation.theme.PollenTheme
import org.koin.compose.viewmodel.koinViewModel

/** ViewModel convenience overload — used by navigation. */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToLocations: () -> Unit = {},
    onNavigateToAllergens: () -> Unit = {},
    onNavigateToFriends: () -> Unit = {},
    onNavigateToReference: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PollenTheme.colors.paper,
    ) { _ ->
        SettingsScreen(
            state = state,
            onBack = onBack,
            onRetry = viewModel::loadData,
            onNavigateToLanguage = onNavigateToLanguage,
            onNavigateToLocations = onNavigateToLocations,
            onNavigateToAllergens = onNavigateToAllergens,
            onNavigateToFriends = onNavigateToFriends,
            onNavigateToReference = onNavigateToReference,
        )
    }
}

/** State-based overload — previewable and testable. */
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit = {},
    onRetry: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToLocations: () -> Unit = {},
    onNavigateToAllergens: () -> Unit = {},
    onNavigateToFriends: () -> Unit = {},
    onNavigateToReference: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
                text = stringResource(MR.strings.settings_title),
                style = MaterialTheme.typography.displaySmall,
                color = PollenTheme.colors.ink,
            )
        }

        when (val data = state.data) {
            is LoadState.Loading -> SettingsSkeleton()
            is LoadState.Failed -> FullScreenError(onRetry = onRetry)
            is LoadState.Loaded -> SettingsContent(
                data = data.data,
                onNavigateToLanguage = onNavigateToLanguage,
                onNavigateToLocations = onNavigateToLocations,
                onNavigateToAllergens = onNavigateToAllergens,
                onNavigateToFriends = onNavigateToFriends,
                onNavigateToReference = onNavigateToReference,
            )
        }
    }
}

@Composable
private fun SettingsContent(
    data: SettingsData,
    onNavigateToLanguage: () -> Unit,
    onNavigateToLocations: () -> Unit,
    onNavigateToAllergens: () -> Unit,
    onNavigateToFriends: () -> Unit,
    onNavigateToReference: () -> Unit,
) {
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(4.dp))

        // Participant code card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(MR.strings.settings_participant_code).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = PollenTheme.colors.ink3,
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = data.participantCode,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = PollenTheme.colors.ink,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(MR.strings.settings_copy),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = PollenTheme.colors.accent2,
                        modifier = Modifier.clickable {
                            if (data.participantCode != "—") {
                                clipboardManager.setText(AnnotatedString(data.participantCode))
                            }
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // "Основные" section
        SettingsGroup(
            header = stringResource(MR.strings.settings_general),
            items = listOf(
                SettingsRowData(stringResource(MR.strings.settings_language), data.languageLabel.localized(), onNavigateToLanguage),
                SettingsRowData(stringResource(MR.strings.settings_monitoring_region), data.regionLabel, onNavigateToLocations),
                SettingsRowData(stringResource(MR.strings.settings_main_allergen), data.mainAllergenLabel, onNavigateToAllergens),
                SettingsRowData(stringResource(MR.strings.settings_friends), data.friendsLabel?.localized() ?: "—", onNavigateToFriends),
            ),
        )

        Spacer(Modifier.height(20.dp))

        // "Информация" section
        SettingsGroup(
            header = stringResource(MR.strings.settings_info),
            items = listOf(
                SettingsRowData(stringResource(MR.strings.settings_allergen_reference), "", onNavigateToReference),
                SettingsRowData(stringResource(MR.strings.settings_guide), "", {
                    uriHandler.openUri("https://pollen.club/guide/")
                }),
                SettingsRowData(stringResource(MR.strings.settings_become_participant), "", {
                    uriHandler.openUri("https://pollen.club/offer/")
                }),
            ),
        )

        Spacer(Modifier.height(24.dp))
    }
}

private data class SettingsRowData(
    val label: String,
    val value: String,
    val onClick: () -> Unit,
)

@Composable
private fun SettingsGroup(
    header: String,
    items: List<SettingsRowData>,
) {
    Text(
        text = header,
        style = MaterialTheme.typography.labelMedium,
        color = PollenTheme.colors.ink3,
        modifier = Modifier.padding(bottom = 8.dp),
    )
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = PollenTheme.colors.line2)
                }
                SettingsRow(
                    label = item.label,
                    value = item.value,
                    onClick = item.onClick,
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.ink,
            modifier = Modifier.weight(1f),
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = PollenTheme.colors.ink3,
            )
            Spacer(Modifier.width(4.dp))
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = PollenTheme.colors.ink3,
            modifier = Modifier.size(16.dp),
        )
    }
}

// region Previews

@Preview
@Composable
private fun PreviewSettingsLoaded() {
    PollenTheme {
        SettingsScreen(
            state = SettingsUiState(
                data = LoadState.Loaded(
                    SettingsData(
                        participantCode = "12345",
                        languageLabel = StringDesc.Raw("Русский"),
                        regionLabel = "Москва",
                        mainAllergenLabel = "Берёза",
                        friendsLabel = StringDesc.Raw("3 друга"),
                    ),
                ),
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsLoading() {
    PollenTheme {
        SettingsScreen(
            state = SettingsUiState(data = LoadState.Loading),
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsFailed() {
    PollenTheme {
        SettingsScreen(
            state = SettingsUiState(data = LoadState.Failed),
        )
    }
}

// endregion

@Composable
private fun SettingsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        // Participant code skeleton
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(
                    Modifier
                        .size(width = 100.dp, height = 12.dp)
                        .shimmerEffect(),
                )
                Spacer(Modifier.height(10.dp))
                Spacer(
                    Modifier
                        .size(width = 120.dp, height = 24.dp)
                        .shimmerEffect(),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Section skeleton
        Spacer(
            Modifier
                .size(width = 80.dp, height = 12.dp)
                .shimmerEffect(),
        )
        Spacer(Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(4) { i ->
                    if (i > 0) Spacer(Modifier.height(20.dp))
                    Spacer(
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(14.dp)
                            .shimmerEffect(),
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Spacer(
            Modifier
                .size(width = 80.dp, height = 12.dp)
                .shimmerEffect(),
        )
        Spacer(Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(3) { i ->
                    if (i > 0) Spacer(Modifier.height(20.dp))
                    Spacer(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .height(14.dp)
                            .shimmerEffect(),
                    )
                }
            }
        }
    }
}
