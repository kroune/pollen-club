package io.github.kroune.pollen.presentation.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.github.kroune.pollen.presentation.common.rememberCopyToClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.presentation.theme.PollenTheme
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun YourCodeForFriends(
    myServerId: String,
    qrSize: Dp = 72.dp,
    onQrClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (myServerId.isBlank()) return

    val copyToClipboard = rememberCopyToClipboard()
    val qrPainter = rememberQrCodePainter(myServerId)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.then(
            if (onQrClick != null) Modifier.clickable(onClick = onQrClick) else Modifier,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = qrPainter,
                contentDescription = null,
                modifier = Modifier.size(qrSize),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                CopyableId(
                    id = myServerId,
                    onCopy = { copyToClipboard(myServerId) },
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = stringResource(MR.strings.friends_show_qr_hint),
                    fontSize = 12.sp,
                    color = PollenTheme.colors.ink3,
                    lineHeight = 17.sp,
                )
            }
        }
    }
}

@Composable
fun CopyableId(
    id: String,
    onCopy: () -> Unit,
    fontSize: TextUnit = 20.sp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = id,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
            letterSpacing = 1.sp,
            color = PollenTheme.colors.ink,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(MR.strings.friends_copy),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.accent2,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(PollenTheme.colors.accentLight)
                .clickable(onClick = onCopy)
                .padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

// region Previews

@Preview
@Composable
private fun PreviewYourCodeForFriends() {
    PollenTheme {
        YourCodeForFriends(myServerId = "1132894")
    }
}

@Preview
@Composable
private fun PreviewCopyableId() {
    PollenTheme {
        CopyableId(id = "1132894", onCopy = {})
    }
}

// endregion
