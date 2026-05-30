package io.github.kroune.pollen.presentation.friends.addfriend

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.presentation.common.CollectEffects
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.friends.QrScanTabContent
import io.github.kroune.pollen.presentation.friends.YourCodeForFriends
import io.github.kroune.pollen.presentation.theme.PollenTheme
import io.github.kroune.pollen.qr.QrScanResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun AddFriendScreen(
    state: AddFriendUiState,
    effects: Flow<UiEvent> = emptyFlow(),
    onIntent: (AddFriendIntent) -> Unit = {},
    onBack: () -> Unit = {},
    onNavigateToMyQr: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects(effects, snackbarHostState)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onBack()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PollenTheme.colors.paper,
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            AddFriendTopBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                SegmentedTabBar(
                    selectedTab = state.selectedTab,
                    onTabSelected = { onIntent(AddFriendIntent.TabSelected(it)) },
                )

                Spacer(Modifier.height(20.dp))

                when (state.selectedTab) {
                    AddFriendTab.QR -> QrTabContent(
                        myServerId = state.myServerId,
                        onQrScanned = { onIntent(AddFriendIntent.QrScanned(it)) },
                        onNavigateToMyQr = onNavigateToMyQr,
                    )
                    AddFriendTab.MANUAL -> ManualTabContent(
                        state = state,
                        onFriendIdChanged = { onIntent(AddFriendIntent.FriendIdChanged(it)) },
                        onNameChanged = { onIntent(AddFriendIntent.NameChanged(it)) },
                        onSubmit = { onIntent(AddFriendIntent.Submit) },
                        onNavigateToMyQr = onNavigateToMyQr,
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmentedTabBar(
    selectedTab: AddFriendTab,
    onTabSelected: (AddFriendTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PollenTheme.colors.paper2)
            .padding(3.dp),
    ) {
        TabSegment(
            label = stringResource(MR.strings.friends_tab_qr),
            isSelected = selectedTab == AddFriendTab.QR,
            onClick = { onTabSelected(AddFriendTab.QR) },
            modifier = Modifier.weight(1f),
        )
        TabSegment(
            label = stringResource(MR.strings.friends_tab_manual),
            isSelected = selectedTab == AddFriendTab.MANUAL,
            onClick = { onTabSelected(AddFriendTab.MANUAL) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TabSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) PollenTheme.colors.card else PollenTheme.colors.paper2,
        animationSpec = tween(200),
        label = "tabBg",
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp,
        animationSpec = tween(200),
        label = "tabElev",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) PollenTheme.colors.ink else PollenTheme.colors.ink3,
        animationSpec = tween(200),
        label = "tabText",
    )

    Box(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor,
        )
    }
}

@Composable
private fun QrTabContent(
    myServerId: String,
    onQrScanned: (QrScanResult) -> Unit,
    onNavigateToMyQr: () -> Unit,
) {
    QrScanTabContent(
        onScanResult = onQrScanned,
    )

    Spacer(Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = PollenTheme.colors.line2)
        Text(
            text = stringResource(MR.strings.friends_or),
            style = MaterialTheme.typography.bodySmall,
            color = PollenTheme.colors.ink3,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = PollenTheme.colors.line2)
    }

    Spacer(Modifier.height(20.dp))

    EyebrowLabel(stringResource(MR.strings.friends_your_code))
    Spacer(Modifier.height(8.dp))
    YourCodeForFriends(myServerId = myServerId, onQrClick = onNavigateToMyQr)
}

@Composable
private fun ManualTabContent(
    state: AddFriendUiState,
    onFriendIdChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateToMyQr: () -> Unit,
) {
    EyebrowLabel(stringResource(MR.strings.friends_id_label))
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = state.friendIdInput,
        onValueChange = onFriendIdChanged,
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PollenTheme.colors.accent,
            unfocusedBorderColor = PollenTheme.colors.accent,
            focusedContainerColor = PollenTheme.colors.card,
            unfocusedContainerColor = PollenTheme.colors.card,
        ),
    )

    Spacer(Modifier.height(20.dp))

    EyebrowLabel(stringResource(MR.strings.friends_name_label))
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = state.nameInput,
        onValueChange = onNameChanged,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PollenTheme.colors.line,
            unfocusedBorderColor = PollenTheme.colors.line,
            focusedContainerColor = PollenTheme.colors.card,
            unfocusedContainerColor = PollenTheme.colors.card,
        ),
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = stringResource(MR.strings.friends_name_hint),
        style = MaterialTheme.typography.labelSmall,
        color = PollenTheme.colors.ink3,
        modifier = Modifier.padding(start = 4.dp),
    )

    Spacer(Modifier.height(24.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(14.dp), ambientColor = PollenTheme.colors.accent)
            .clip(RoundedCornerShape(14.dp))
            .background(PollenTheme.colors.accent)
            .clickable(enabled = !state.isSubmitting && state.friendIdInput.isNotBlank()) {
                onSubmit()
            }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(MR.strings.friends_add_button),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }

    Spacer(Modifier.height(28.dp))

    HorizontalDivider(color = PollenTheme.colors.line2)

    Spacer(Modifier.height(20.dp))

    EyebrowLabel(stringResource(MR.strings.friends_your_code))
    Spacer(Modifier.height(8.dp))
    YourCodeForFriends(myServerId = state.myServerId, onQrClick = onNavigateToMyQr)
}

@Composable
private fun AddFriendTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = PollenTheme.colors.ink,
            )
        }
        Text(
            text = stringResource(MR.strings.friends_add_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
private fun EyebrowLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = PollenTheme.colors.ink3,
    )
}

// region Previews

@Preview
@Composable
private fun PreviewAddFriendManualTab() {
    PollenTheme {
        AddFriendScreen(
            state = AddFriendUiState(
                selectedTab = AddFriendTab.MANUAL,
                myServerId = "1132894",
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewAddFriendManualFilled() {
    PollenTheme {
        AddFriendScreen(
            state = AddFriendUiState(
                selectedTab = AddFriendTab.MANUAL,
                friendIdInput = "67890",
                nameInput = "Маша",
                myServerId = "1132894",
            ),
        )
    }
}

// endregion
