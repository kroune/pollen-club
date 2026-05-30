package io.github.kroune.pollen.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.presentation.common.CollectEffects
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun RegionSelectorScreen(
    state: RegionSelectorUiState,
    effects: Flow<UiEvent> = emptyFlow(),
    onIntent: (RegionSelectorIntent) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects(effects, snackbarHostState, onRetry = { onIntent(RegionSelectorIntent.LoadData) })

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 4.dp),
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
                text = stringResource(MR.strings.settings_monitoring_region),
                style = MaterialTheme.typography.displaySmall,
                color = PollenTheme.colors.ink,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onIntent(RegionSelectorIntent.SearchQueryChanged(it)) },
            placeholder = {
                Text(
                    stringResource(MR.strings.region_search_placeholder),
                    color = PollenTheme.colors.ink3,
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = PollenTheme.colors.ink3,
                    modifier = Modifier.size(18.dp),
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PollenTheme.colors.accent,
                unfocusedBorderColor = PollenTheme.colors.line2,
                focusedContainerColor = PollenTheme.colors.paper2,
                unfocusedContainerColor = PollenTheme.colors.paper2,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        when (val locations = state.locations) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PollenTheme.colors.accent)
                }
            }
            is LoadState.Failed -> FullScreenError(onRetry = { onIntent(RegionSelectorIntent.LoadData) })
            is LoadState.Loaded -> {
                RegionList(
                    locations = locations.data,
                    selectedLocationId = state.selectedLocationId,
                    onSelect = { onIntent(RegionSelectorIntent.SelectLocation(it)) },
                )
            }
        }
        }
    }
}

// region Previews

private val previewLocations = persistentListOf(
    LocationDomain(1, "Москва", "Центральный регион", 55.7558, 37.6173),
    LocationDomain(2, "Санкт-Петербург", "Северо-Западный регион", 59.9343, 30.3351),
    LocationDomain(3, "Казань", "Поволжье", 55.7961, 49.1089),
    LocationDomain(4, "Краснодар", "Южный регион", 45.0353, 38.9753),
)

@Preview
@Composable
private fun PreviewRegionSelectorLoaded() {
    PollenTheme {
        RegionSelectorScreen(
            state = RegionSelectorUiState(
                locations = LoadState.Loaded(previewLocations),
                selectedLocationId = 1,
                searchQuery = "",
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewRegionSelectorLoading() {
    PollenTheme {
        RegionSelectorScreen(
            state = RegionSelectorUiState(
                locations = LoadState.Loading,
                searchQuery = "",
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewRegionSelectorFailed() {
    PollenTheme {
        RegionSelectorScreen(
            state = RegionSelectorUiState(
                locations = LoadState.Failed,
                searchQuery = "",
            ),
        )
    }
}

// endregion

@Composable
private fun RegionList(
    locations: ImmutableList<LocationDomain>,
    selectedLocationId: Int?,
    onSelect: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        LazyColumn {
            itemsIndexed(locations, key = { _, loc -> loc.id }) { index, location ->
                val isSelected = location.id == selectedLocationId
                if (index > 0) {
                    HorizontalDivider(color = PollenTheme.colors.line2)
                }
                RegionRow(
                    location = location,
                    isSelected = isSelected,
                    onClick = { onSelect(location.id) },
                )
            }
        }
    }
}

@Composable
private fun RegionRow(
    location: LocationDomain,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) PollenTheme.colors.accentLight else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) PollenTheme.colors.accent2 else PollenTheme.colors.ink,
            )
            if (location.description.isNotBlank()) {
                Text(
                    text = location.description,
                    fontSize = 10.sp,
                    color = PollenTheme.colors.ink3,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (isSelected) {
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(PollenTheme.colors.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}
