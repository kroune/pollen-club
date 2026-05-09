package io.github.kroune.pollen.presentation.settings

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.presentation.theme.PollenTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsLanguageScreen(
    viewModel: SettingsLanguageViewModel = koinViewModel(),
    onBack: () -> Unit = {},
) {
    val currentLocale by viewModel.locale.collectAsState()

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
                    contentDescription = "Назад",
                    tint = PollenTheme.colors.ink2,
                    modifier = Modifier.size(22.dp),
                )
            }
            Text(
                text = "Язык",
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
                    label = "Русский",
                    isSelected = currentLocale == AppLocale.RU,
                    onClick = { viewModel.setLocale(AppLocale.RU) },
                )
                HorizontalDivider(color = PollenTheme.colors.line2)
                LanguageRow(
                    label = "English",
                    isSelected = currentLocale == AppLocale.EN,
                    onClick = { viewModel.setLocale(AppLocale.EN) },
                )
            }
        }
    }
}

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
