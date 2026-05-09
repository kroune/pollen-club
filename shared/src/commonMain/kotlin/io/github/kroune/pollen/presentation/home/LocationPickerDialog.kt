package io.github.kroune.pollen.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.kroune.pollen.domain.model.LocationDomain
import io.github.kroune.pollen.presentation.theme.PollenTheme

@Composable
fun LocationPickerDialog(
    locations: List<LocationDomain>,
    selectedLocation: LocationDomain?,
    onSelect: (LocationDomain) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = if (query.isBlank()) locations
    else locations.filter { it.name.contains(query, ignoreCase = true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = PollenTheme.colors.card,
            shadowElevation = 8.dp,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Выбор города",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PollenTheme.colors.ink,
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            "Поиск…",
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
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(filtered, key = { it.id }) { location ->
                        val isSelected = location.id == selectedLocation?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(location) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = location.name,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) PollenTheme.colors.accent2 else PollenTheme.colors.ink,
                                modifier = Modifier.weight(1f),
                            )
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PollenTheme.colors.accent,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                        HorizontalDivider(color = PollenTheme.colors.line2)
                    }
                }
                Spacer(Modifier.height(12.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        "Закрыть",
                        color = PollenTheme.colors.accent2,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
