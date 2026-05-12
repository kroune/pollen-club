package io.github.kroune.pollen.presentation.reference

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.shimmerEffect
import io.github.kroune.pollen.presentation.theme.PollenTheme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceScreen(
    viewModel: ReferenceViewModel = koinViewModel(),
    onBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedAllergen by remember { mutableStateOf<ReferenceAllergenUi?>(null) }

    CollectEvents(viewModel.events, snackbarHostState)

    BackHandler(enabled = selectedAllergen != null) {
        selectedAllergen = null
    }

    // Allergen detail bottom sheet
    if (selectedAllergen != null) {
        val allergen = selectedAllergen!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { selectedAllergen = null },
            modifier = Modifier.statusBarsPadding(),
            sheetState = sheetState,
            containerColor = PollenTheme.colors.card,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {
            AllergenDetailSheet(allergen = allergen)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PollenTheme.colors.paper,
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PollenTheme.colors.card)
                    .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PollenTheme.colors.paper2, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(MR.strings.back),
                        tint = PollenTheme.colors.ink2,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Text(
                    text = stringResource(MR.strings.reference_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                    color = PollenTheme.colors.ink,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PollenTheme.colors.paper2, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            showSearch = !showSearch
                            if (!showSearch) {
                                viewModel.onSearchQueryChange("")
                                keyboardController?.hide()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (showSearch) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (showSearch) stringResource(MR.strings.close_search) else stringResource(MR.strings.search),
                        tint = PollenTheme.colors.ink2,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }

            HorizontalDivider(color = PollenTheme.colors.line2, thickness = 1.dp)

            // Search bar
            AnimatedVisibility(
                visible = showSearch,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = {
                        Text(
                            stringResource(MR.strings.reference_search_placeholder),
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
                    trailingIcon = if (state.searchQuery.isNotEmpty()) {
                        {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .clickable { viewModel.onSearchQueryChange("") },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(MR.strings.clear),
                                    tint = PollenTheme.colors.ink3,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    } else {
                        null
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PollenTheme.colors.paper2,
                        unfocusedContainerColor = PollenTheme.colors.paper2,
                        focusedBorderColor = PollenTheme.colors.accent,
                        unfocusedBorderColor = PollenTheme.colors.line2,
                        cursorColor = PollenTheme.colors.accent,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                )
            }

            // Body
            when (val allergens = state.allergens) {
                is LoadState.Loading -> ReferenceSkeleton()
                is LoadState.Failed -> FullScreenError(onRetry = {})
                is LoadState.Loaded -> {
                    val filtered = remember(allergens.data, state.searchQuery) {
                        if (state.searchQuery.isBlank()) {
                            allergens.data
                        } else {
                            allergens.data.filter {
                                it.name.contains(state.searchQuery, ignoreCase = true)
                            }
                        }
                    }
                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(MR.strings.reference_nothing_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = PollenTheme.colors.ink3,
                            )
                        }
                    } else {
                        ReferenceGrid(
                            allergens = filtered,
                            onAllergenClick = { allergen -> selectedAllergen = allergen },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllergenDetailSheet(allergen: ReferenceAllergenUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        // Header: icon + name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Allergen icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(PollenTheme.colors.paper2, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (allergen.iconRes != null) {
                    Image(
                        painter = painterResource(allergen.iconRes),
                        contentDescription = allergen.name,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Column {
                Text(
                    text = allergen.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PollenTheme.colors.ink,
                )
                if (allergen.nameEng.isNotBlank()) {
                    Text(
                        text = allergen.nameEng,
                        fontSize = 13.sp,
                        color = PollenTheme.colors.ink3,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Description
        if (allergen.description.isNotBlank()) {
            Text(
                text = allergen.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = PollenTheme.colors.ink2,
            )
        } else {
            Text(
                text = stringResource(MR.strings.reference_description_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                color = PollenTheme.colors.ink3,
            )
        }
    }
}

@Composable
private fun ReferenceGrid(
    allergens: List<ReferenceAllergenUi>,
    onAllergenClick: (ReferenceAllergenUi) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            items = allergens,
            key = { it.pollenId },
        ) { allergen ->
            ReferenceAllergenCard(
                allergen = allergen,
                onClick = { onAllergenClick(allergen) },
            )
        }
    }
}

@Composable
private fun ReferenceAllergenCard(
    allergen: ReferenceAllergenUi,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(PollenTheme.colors.paper2, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (allergen.iconRes != null) {
                    Image(
                        painter = painterResource(allergen.iconRes),
                        contentDescription = allergen.name,
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = allergen.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PollenTheme.colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(3.dp))

            if (allergen.severityLevel > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                PollenTheme.colors.severityColor(allergen.severityLevel),
                                CircleShape,
                            ),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = allergen.severityLabel.localized(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PollenTheme.colors.severityColor(allergen.severityLevel),
                        letterSpacing = 0.2.sp,
                    )
                }
            } else {
                Text(
                    text = stringResource(MR.strings.status_not_active),
                    fontSize = 10.sp,
                    color = PollenTheme.colors.ink3,
                )
            }
        }
    }
}

@Composable
private fun ReferenceSkeleton() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        userScrollEnabled = false,
    ) {
        items(6) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shimmerEffect(),
                    )
                    Spacer(Modifier.height(10.dp))
                    Spacer(
                        Modifier
                            .width(80.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                    Spacer(Modifier.height(6.dp))
                    Spacer(
                        Modifier
                            .width(60.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                }
            }
        }
    }
}
