package io.github.kroune.pollen.presentation.sensitivity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.presentation.common.CollectEffects
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.common.shimmerEffect
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
private fun sensitivityLabel(level: SensitivityLevel): String = when (level) {
    SensitivityLevel.NONE -> stringResource(MR.strings.sensitivity_none)
    SensitivityLevel.LIGHT -> stringResource(MR.strings.sensitivity_light)
    SensitivityLevel.MODERATE -> stringResource(MR.strings.sensitivity_moderate)
    SensitivityLevel.SEVERE -> stringResource(MR.strings.sensitivity_strong)
}

@Composable
fun SensitivityScreen(
    state: SensitivityUiState,
    effects: Flow<UiEvent> = emptyFlow(),
    onBack: () -> Unit = {},
    onRetry: () -> Unit = {},
    onSetSensitivity: (pollenId: Int, level: SensitivityLevel) -> Unit = { _, _ -> },
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects(effects, snackbarHostState, onRetry = onRetry)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PollenTheme.colors.paper,
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 0.dp, bottom = 0.dp),
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
                text = stringResource(MR.strings.sensitivity_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = PollenTheme.colors.ink,
            )
        }

        Text(
            text = stringResource(MR.strings.sensitivity_description),
            style = MaterialTheme.typography.bodyMedium,
            color = PollenTheme.colors.ink3,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        )

        when (val allergens = state.allergens) {
            is LoadState.Loading -> SensitivitySkeleton()
            is LoadState.Failed -> FullScreenError(onRetry = onRetry)
            is LoadState.Loaded -> SensitivityList(
                allergens = allergens.data,
                onSetLevel = onSetSensitivity,
            )
        }
        }
    }
}

@Composable
private fun SensitivityList(
    allergens: ImmutableList<SensitivityAllergenUi>,
    onSetLevel: (pollenId: Int, level: SensitivityLevel) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        LazyColumn {
            itemsIndexed(
                items = allergens,
                key = { _, item -> item.pollenId },
            ) { index, allergen ->
                if (index > 0) {
                    HorizontalDivider(color = PollenTheme.colors.line2)
                }
                SensitivityRow(
                    allergen = allergen,
                    onSetLevel = { level -> onSetLevel(allergen.pollenId, level) },
                )
            }
        }
    }
}

@Composable
private fun SensitivityRow(
    allergen: SensitivityAllergenUi,
    onSetLevel: (SensitivityLevel) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = allergen.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.ink,
            modifier = Modifier.width(100.dp),
            maxLines = 1,
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            SensitivityLevel.entries.forEach { level ->
                val filled = level.value <= allergen.level.value
                val barShape = RoundedCornerShape(3.dp)
                val interactionSource = remember { MutableInteractionSource() }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(barShape)
                        .background(
                            if (filled) PollenTheme.colors.accent else PollenTheme.colors.line2,
                            barShape,
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) {
                            if (level == allergen.level && level != SensitivityLevel.NONE) {
                                onSetLevel(SensitivityLevel.NONE)
                            } else {
                                onSetLevel(level)
                            }
                        },
                )
            }
        }

        Text(
            text = sensitivityLabel(allergen.level),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            color = if (allergen.level != SensitivityLevel.NONE) {
                PollenTheme.colors.ink2
            } else {
                PollenTheme.colors.ink3
            },
            modifier = Modifier.width(52.dp),
        )
    }
}

// region Previews

private val previewAllergens = persistentListOf(
    SensitivityAllergenUi(1, "Берёза", SensitivityLevel.SEVERE),
    SensitivityAllergenUi(2, "Орешник", SensitivityLevel.MODERATE),
    SensitivityAllergenUi(3, "Ольха", SensitivityLevel.LIGHT),
    SensitivityAllergenUi(4, "Дуб", SensitivityLevel.NONE),
    SensitivityAllergenUi(5, "Полынь", SensitivityLevel.NONE),
)

@Preview
@Composable
private fun PreviewSensitivityLoaded() {
    PollenTheme {
        SensitivityScreen(
            state = SensitivityUiState(allergens = LoadState.Loaded(previewAllergens)),
        )
    }
}

@Preview
@Composable
private fun PreviewSensitivityLoading() {
    PollenTheme {
        SensitivityScreen(
            state = SensitivityUiState(allergens = LoadState.Loading),
        )
    }
}

@Preview
@Composable
private fun PreviewSensitivityFailed() {
    PollenTheme {
        SensitivityScreen(
            state = SensitivityUiState(allergens = LoadState.Failed),
        )
    }
}

// endregion

@Composable
private fun SensitivitySkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            repeat(8) { i ->
                if (i > 0) Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(
                        modifier = Modifier
                            .width(72.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                    Spacer(Modifier.width(10.dp))
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        repeat(4) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .shimmerEffect(),
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Spacer(
                        modifier = Modifier
                            .width(40.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                }
            }
        }
    }
}
