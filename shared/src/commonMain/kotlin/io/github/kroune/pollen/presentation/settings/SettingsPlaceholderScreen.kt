package io.github.kroune.pollen.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import androidx.compose.ui.tooling.preview.Preview
import io.github.kroune.pollen.presentation.theme.PollenTheme

@Composable
fun SettingsPlaceholderScreen(
    title: String,
    onBack: () -> Unit = {},
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
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = PollenTheme.colors.ink,
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(MR.strings.settings_coming_soon),
                style = MaterialTheme.typography.bodyLarge,
                color = PollenTheme.colors.ink3,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSettingsPlaceholder() {
    PollenTheme {
        SettingsPlaceholderScreen(title = "Уведомления")
    }
}
