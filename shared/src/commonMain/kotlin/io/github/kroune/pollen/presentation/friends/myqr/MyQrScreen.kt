package io.github.kroune.pollen.presentation.friends.myqr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.presentation.common.CollectEffects
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.MaxBrightnessEffect
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.common.rememberCopyToClipboard
import io.github.kroune.pollen.presentation.common.rememberShareTextLauncher
import io.github.kroune.pollen.presentation.friends.CopyableId
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun MyQrScreen(
    state: MyQrUiState,
    effects: Flow<UiEvent> = emptyFlow(),
    onBack: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects(effects, snackbarHostState, onRetry = onRetry)

    MaxBrightnessEffect()

    val copyToClipboard = rememberCopyToClipboard()
    val shareText = rememberShareTextLauncher()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PollenTheme.colors.paper,
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = stringResource(MR.strings.friends_my_qr_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.size(48.dp))
            }

            when (val serverId = state.myServerId) {
                is LoadState.Loading -> {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PollenTheme.colors.accent)
                    }
                }
                is LoadState.Failed -> FullScreenError(onRetry = onRetry)
                is LoadState.Loaded -> {
                    val id = serverId.data
                    if (id.isNotBlank()) {
                        MyQrContent(
                            myServerId = id,
                            copyToClipboard = copyToClipboard,
                            shareText = shareText,
                        )
                    } else {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(MR.strings.friends_no_id),
                                style = MaterialTheme.typography.bodyMedium,
                                color = PollenTheme.colors.ink3,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyQrContent(
    myServerId: String,
    copyToClipboard: (String) -> Unit,
    shareText: (String) -> Unit,
) {
    val qrPainter = rememberQrCodePainter(myServerId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = qrPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(MR.strings.friends_your_id_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = PollenTheme.colors.ink3,
                )
                Spacer(Modifier.height(6.dp))
                CopyableId(
                    id = myServerId,
                    onCopy = { copyToClipboard(myServerId) },
                    fontSize = 24.sp,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(MR.strings.friends_my_qr_instruction),
            style = MaterialTheme.typography.bodySmall,
            color = PollenTheme.colors.ink3,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .shadow(6.dp, RoundedCornerShape(14.dp), ambientColor = PollenTheme.colors.accent)
                .clip(RoundedCornerShape(14.dp))
                .background(PollenTheme.colors.accent)
                .clickable { shareText(myServerId) }
                .padding(horizontal = 32.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(MR.strings.friends_share),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// region Previews

@Preview
@Composable
private fun PreviewMyQrLoaded() {
    PollenTheme {
        MyQrScreen(
            state = MyQrUiState(myServerId = LoadState.Loaded("1132894")),
            onBack = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewMyQrLoading() {
    PollenTheme {
        MyQrScreen(
            state = MyQrUiState(myServerId = LoadState.Loading),
            onBack = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewMyQrFailed() {
    PollenTheme {
        MyQrScreen(
            state = MyQrUiState(myServerId = LoadState.Failed),
            onBack = {},
            onRetry = {},
        )
    }
}

// endregion
