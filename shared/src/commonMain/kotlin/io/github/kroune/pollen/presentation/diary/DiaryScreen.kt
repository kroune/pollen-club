package io.github.kroune.pollen.presentation.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.pollen.domain.model.BodyZone
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    onNavigateToMedications: () -> Unit = {},
    viewModel: DiaryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEvents(viewModel.events, snackbarHostState)

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DiaryDatePickerDialog(
            selectedIsoDate = state.selectedIsoDate,
            onDateSelected = { isoDate ->
                showDatePicker = false
                viewModel.selectDate(isoDate)
            },
            onDismiss = { showDatePicker = false },
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            DateHeader(
                monthLabel = state.monthLabel,
                dates = state.dates,
                onDateSelected = viewModel::selectDate,
                onPreviousWeek = { viewModel.navigateWeek(forward = false) },
                onNextWeek = { viewModel.navigateWeek(forward = true) },
                onCalendarClick = { showDatePicker = true },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = 16.dp),
            ) {
                MoodSection(state.moodOptions, viewModel::selectFeeling)

                val hasMood = state.moodOptions.any { it.isSelected }
                if (hasMood) {
                    Spacer(Modifier.height(16.dp))
                    BodySymptomsSection(
                        bodyZones = state.bodyZones,
                        selectedZoneLabel = state.selectedZoneLabel,
                        selectedZoneTags = state.selectedZoneTags,
                        onZoneSelected = viewModel::selectZone,
                        onTagToggled = viewModel::toggleTag,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = PollenTheme.colors.line2,
                    )
                } else {
                    Spacer(Modifier.height(20.dp))
                }

                TherapySection(
                    items = state.therapyItems,
                    onToggleTaken = viewModel::toggleMedicationTaken,
                    onAddMedication = onNavigateToMedications,
                )
            }
        }
    }
}

// region Date header

@Composable
private fun DateHeader(
    monthLabel: String,
    dates: ImmutableList<DiaryDateUi>,
    onDateSelected: (String) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCalendarClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 6.dp, top = 12.dp, bottom = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = monthLabel.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = PollenTheme.colors.ink3,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPreviousWeek, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = PollenTheme.colors.ink3,
                        )
                    }
                    IconButton(onClick = onCalendarClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = PollenTheme.colors.ink3,
                        )
                    }
                    IconButton(onClick = onNextWeek, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = PollenTheme.colors.ink3,
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val shape = RoundedCornerShape(10.dp)
                dates.forEach { date ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .background(
                                if (date.isSelected) PollenTheme.colors.accent else Color.Transparent,
                                shape,
                            )
                            .clickable { onDateSelected(date.isoDate) }
                            .padding(vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = date.dayOfWeek,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (date.isSelected) {
                                Color.White.copy(alpha = 0.7f)
                            } else {
                                PollenTheme.colors.ink3
                            },
                        )
                        Text(
                            text = "${date.dayOfMonth}",
                            fontSize = 13.sp,
                            fontWeight = if (date.isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (date.isSelected) Color.White else PollenTheme.colors.ink2,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = PollenTheme.colors.line2)
    }
}

// endregion

// region Mood

@Composable
private fun MoodSection(
    options: ImmutableList<DiaryMoodOptionUi>,
    onMoodSelected: (Feeling) -> Unit,
) {
    Text(
        text = "САМОЧУВСТВИЕ",
        style = MaterialTheme.typography.labelMedium,
        color = PollenTheme.colors.ink3,
    )
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        options.forEach { option ->
            MoodPill(
                option = option,
                onClick = { onMoodSelected(option.feeling) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MoodPill(
    option: DiaryMoodOptionUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val severityColor = when (option.feeling) {
        Feeling.GOOD -> PollenTheme.colors.severity1
        Feeling.MIDDLE -> PollenTheme.colors.severity2
        Feeling.BAD -> PollenTheme.colors.severity3
    }
    val borderColor = if (option.isSelected) severityColor else PollenTheme.colors.line2
    val bgColor = if (option.isSelected) severityColor.copy(alpha = 0.08f) else Color.Transparent
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(1.5.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(severityColor, CircleShape),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = option.label,
            fontSize = 12.sp,
            fontWeight = if (option.isSelected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

// endregion

// region Body & symptoms

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BodySymptomsSection(
    bodyZones: ImmutableList<DiaryBodyZoneUi>,
    selectedZoneLabel: String?,
    selectedZoneTags: ImmutableList<DiarySymptomTagUi>,
    onZoneSelected: (BodyZone) -> Unit,
    onTagToggled: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        BodyZonePlaceholder(
            bodyZones = bodyZones,
            onZoneSelected = onZoneSelected,
            modifier = Modifier.width(110.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            if (selectedZoneLabel != null) {
                Text(
                    text = selectedZoneLabel.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = PollenTheme.colors.ink3,
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    selectedZoneTags.forEach { tag ->
                        SymptomTagPill(tag = tag, onClick = { onTagToggled(tag.key) })
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "+ другую зону",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = PollenTheme.colors.accent2,
                    modifier = Modifier.clickable {
                        bodyZones.firstOrNull { it.isSelected }?.let { onZoneSelected(it.zone) }
                    },
                )
            } else {
                Text(
                    text = "Выберите зону тела",
                    style = MaterialTheme.typography.bodySmall,
                    color = PollenTheme.colors.ink3,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun BodyZonePlaceholder(
    bodyZones: ImmutableList<DiaryBodyZoneUi>,
    onZoneSelected: (BodyZone) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        bodyZones.forEach { zone ->
            val hasSymptoms = zone.symptomCount > 0
            val shape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(
                        when {
                            zone.isSelected -> PollenTheme.colors.accentLight
                            hasSymptoms -> PollenTheme.colors.severity3.copy(alpha = 0.08f)
                            else -> Color.Transparent
                        },
                        shape,
                    )
                    .border(
                        width = if (zone.isSelected) 1.5.dp else 0.8.dp,
                        color = when {
                            zone.isSelected -> PollenTheme.colors.accent
                            hasSymptoms -> PollenTheme.colors.severity3.copy(alpha = 0.5f)
                            else -> PollenTheme.colors.line
                        },
                        shape = shape,
                    )
                    .clickable { onZoneSelected(zone.zone) }
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = zone.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (zone.isSelected) PollenTheme.colors.accent2 else PollenTheme.colors.ink2,
                    )
                    if (hasSymptoms) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${zone.symptomCount}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PollenTheme.colors.severity3,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SymptomTagPill(
    tag: DiarySymptomTagUi,
    onClick: () -> Unit,
) {
    val bg = if (tag.isSelected) PollenTheme.colors.accent else PollenTheme.colors.paper2
    val textColor = if (tag.isSelected) Color.White else PollenTheme.colors.ink2
    val borderColor = if (tag.isSelected) PollenTheme.colors.accent else PollenTheme.colors.line2
    val shape = RoundedCornerShape(100.dp)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 10.dp),
    ) {
        Text(
            text = tag.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
        )
    }
}

// endregion

// region Date picker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryDatePickerDialog(
    selectedIsoDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialMillis = if (selectedIsoDate.isNotBlank()) {
        LocalDate.parse(selectedIsoDate).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    } else {
        null
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    val pickerColors = DatePickerDefaults.colors(
        containerColor = PollenTheme.colors.card,
        titleContentColor = PollenTheme.colors.ink3,
        headlineContentColor = PollenTheme.colors.ink,
        weekdayContentColor = PollenTheme.colors.ink3,
        subheadContentColor = PollenTheme.colors.ink2,
        navigationContentColor = PollenTheme.colors.ink3,
        todayDateBorderColor = PollenTheme.colors.accent,
        todayContentColor = PollenTheme.colors.accent,
        selectedDayContainerColor = PollenTheme.colors.accent,
        selectedDayContentColor = Color.White,
        dividerColor = PollenTheme.colors.line2,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = Instant.fromEpochMilliseconds(millis)
                        .toLocalDateTime(TimeZone.UTC).date
                    onDateSelected(date.toString())
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = pickerColors,
    ) {
        DatePicker(
            state = datePickerState,
            colors = pickerColors,
            showModeToggle = false,
        )
    }
}

// endregion

// region Therapy

@Composable
private fun TherapySection(
    items: ImmutableList<DiaryTherapyItemUi>,
    onToggleTaken: (Long) -> Unit,
    onAddMedication: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "ТЕРАПИЯ",
            style = MaterialTheme.typography.labelMedium,
            color = PollenTheme.colors.ink3,
        )
        Text(
            text = "+ препарат",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.accent2,
            modifier = Modifier.clickable(onClick = onAddMedication),
        )
    }

    Spacer(Modifier.height(10.dp))

    if (items.isEmpty()) {
        Text(
            text = "Нет назначенных препаратов",
            style = MaterialTheme.typography.bodySmall,
            color = PollenTheme.colors.ink3,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = PollenTheme.colors.line2)
                }
                TherapyRow(item = item, onToggleTaken = { onToggleTaken(item.therapyId) })
            }
        }
    }
}

@Composable
private fun TherapyRow(
    item: DiaryTherapyItemUi,
    onToggleTaken: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleTaken)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.time.isNotBlank()) {
            Text(
                text = item.time,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = PollenTheme.colors.ink3,
                modifier = Modifier.width(34.dp),
            )
        }

        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(
                    if (item.taken) PollenTheme.colors.accent else Color.Transparent,
                    CircleShape,
                )
                .then(
                    if (!item.taken) Modifier.border(1.5.dp, PollenTheme.colors.line, CircleShape)
                    else Modifier,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (item.taken) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = Color.White,
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            if (item.dosage.isNotBlank()) {
                Text(
                    text = item.dosage,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = PollenTheme.colors.ink3,
                )
            }
        }
    }
}

// endregion
