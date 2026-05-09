package io.github.kroune.pollen.presentation.medications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MedicationsScreen(
    onBack: () -> Unit = {},
    viewModel: MedicationsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEvents(viewModel.events, snackbarHostState)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PollenTheme.colors.paper,
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar(onBack = onBack)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(top = 14.dp, bottom = 64.dp),
                ) {
                    SearchField(
                        query = state.searchQuery,
                        onQueryChanged = viewModel::onSearchQueryChanged,
                    )
                    Spacer(Modifier.height(18.dp))

                    SectionHeader("Ваши препараты")
                    Spacer(Modifier.height(8.dp))
                    RecentMedsList(
                        meds = state.recentMeds,
                        onToggleTaken = viewModel::toggleTakenToday,
                    )

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = PollenTheme.colors.line2)
                    Spacer(Modifier.height(12.dp))

                    SectionHeader("Категории")
                    Spacer(Modifier.height(8.dp))
                    CategoriesCard(categories = state.categories)
                }
            }

            if (state.isSheetExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(onClick = viewModel::toggleSheetExpanded),
                )
            }

            TodaySheet(
                doses = state.todayDoses,
                todayCount = state.todayCount,
                isExpanded = state.isSheetExpanded,
                onToggleExpand = viewModel::toggleSheetExpanded,
                onRemoveDose = viewModel::removeTodayDose,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = PollenTheme.colors.ink2,
            )
        }
        Text(
            text = "Препарат",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PollenTheme.colors.ink,
        )
    }
    HorizontalDivider(color = PollenTheme.colors.line2)
}

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(PollenTheme.colors.card, shape)
            .border(1.dp, PollenTheme.colors.line2, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = PollenTheme.colors.ink3,
        )
        Spacer(Modifier.width(10.dp))
        if (query.isEmpty()) {
            Text(
                "Поиск препарата",
                fontSize = 13.sp,
                color = PollenTheme.colors.ink3,
            )
        } else {
            Text(
                query,
                fontSize = 13.sp,
                color = PollenTheme.colors.ink,
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = PollenTheme.colors.ink3,
        letterSpacing = 0.6.sp,
    )
}

@Composable
private fun RecentMedsList(
    meds: ImmutableList<RecentMedUi>,
    onToggleTaken: (Long) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        meds.forEach { med ->
            RecentMedRow(med = med, onToggleTaken = { onToggleTaken(med.therapyId) })
        }
    }
}

@Composable
private fun RecentMedRow(
    med: RecentMedUi,
    onToggleTaken: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor = if (med.takenToday) PollenTheme.colors.accent else PollenTheme.colors.line2
    val bgColor = if (med.takenToday) PollenTheme.colors.accentLight else PollenTheme.colors.card
    val borderWidth = if (med.takenToday) 1.5.dp else 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor, shape)
            .border(borderWidth, borderColor, shape)
            .clickable(onClick = onToggleTaken)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MedInitialBadge(
            initial = med.name.first(),
            isTaken = med.takenToday,
        )

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = med.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = PollenTheme.colors.ink,
            )
            Text(
                text = med.substance,
                fontSize = 10.sp,
                color = PollenTheme.colors.ink3,
                modifier = Modifier.padding(top = 1.dp),
            )
            Text(
                text = "${med.count} приёмов · ${med.lastTaken}",
                fontSize = 9.sp,
                color = PollenTheme.colors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        Spacer(Modifier.width(8.dp))

        val pillBg = if (med.takenToday) Color.Transparent else PollenTheme.colors.accent
        val pillTextColor = if (med.takenToday) PollenTheme.colors.ink3 else Color.White
        val pillBorder = if (med.takenToday) PollenTheme.colors.line2 else Color.Transparent
        val pillShape = RoundedCornerShape(100.dp)

        Box(
            modifier = Modifier
                .clip(pillShape)
                .background(pillBg, pillShape)
                .border(if (med.takenToday) 1.dp else 0.dp, pillBorder, pillShape)
                .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Text(
                text = if (med.takenToday) "отменить" else "+ принять",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = pillTextColor,
            )
        }
    }
}

@Composable
private fun MedInitialBadge(initial: Char, isTaken: Boolean) {
    val bg = if (isTaken) PollenTheme.colors.accent else PollenTheme.colors.paper2
    val contentColor = if (isTaken) Color.White else PollenTheme.colors.ink3
    val borderColor = if (isTaken) Color.Transparent else PollenTheme.colors.line2

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(bg, CircleShape)
            .then(if (!isTaken) Modifier.border(1.dp, borderColor, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (isTaken) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = contentColor,
            )
        } else {
            Text(
                text = "$initial",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
            )
        }
    }
}

@Composable
private fun CategoriesCard(categories: ImmutableList<MedCategoryUi>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        categories.forEachIndexed { index, category ->
            if (index > 0) {
                HorizontalDivider(color = PollenTheme.colors.line2)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: navigate to category detail */ }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = category.name,
                    fontSize = 13.sp,
                    color = PollenTheme.colors.ink,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = PollenTheme.colors.ink3,
                )
            }
        }
    }
}

// region Bottom sheet

@Composable
private fun TodaySheet(
    doses: ImmutableList<TodayDoseUi>,
    todayCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onRemoveDose: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (doses.isEmpty()) return

    val shape = if (isExpanded) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    } else {
        RoundedCornerShape(0.dp)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, shape)
            .background(PollenTheme.colors.card, shape),
    ) {
        if (isExpanded) {
            ExpandedSheetContent(
                doses = doses,
                onToggleExpand = onToggleExpand,
                onRemoveDose = onRemoveDose,
            )
        } else {
            CollapsedSheetContent(
                doses = doses,
                todayCount = todayCount,
                onToggleExpand = onToggleExpand,
            )
        }
    }
}

@Composable
private fun CollapsedSheetContent(
    doses: ImmutableList<TodayDoseUi>,
    todayCount: Int,
    onToggleExpand: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy((-7).dp)) {
            doses.forEach { dose ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PollenTheme.colors.accent, CircleShape)
                        .border(2.dp, PollenTheme.colors.card, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${dose.initial}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        Row(modifier = Modifier.weight(1f)) {
            Text(
                text = "Сегодня",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PollenTheme.colors.ink,
            )
            Text(
                text = " · $todayCount приёма",
                fontSize = 12.sp,
                color = PollenTheme.colors.ink3,
            )
        }

        Text(
            text = "детали",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.accent2,
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(11.dp),
            tint = PollenTheme.colors.accent2,
        )
    }
}

@Composable
private fun ExpandedSheetContent(
    doses: ImmutableList<TodayDoseUi>,
    onToggleExpand: () -> Unit,
    onRemoveDose: (Long) -> Unit,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PollenTheme.colors.line),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Сегодня",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PollenTheme.colors.ink,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = todayDateLabel(),
                fontSize = 11.sp,
                color = PollenTheme.colors.ink3,
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            doses.forEach { dose ->
                TodayDoseCard(dose = dose, onRemove = { onRemoveDose(dose.therapyId) })
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun TodayDoseCard(
    dose: TodayDoseUi,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PollenTheme.colors.line2, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PollenTheme.colors.accent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${dose.initial}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = dose.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PollenTheme.colors.ink,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = dose.dosage,
                    fontSize = 11.sp,
                    color = PollenTheme.colors.ink3,
                )

                Spacer(Modifier.width(6.dp))

                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(12.dp)
                        .clickable(onClick = onRemove),
                    tint = PollenTheme.colors.ink3,
                )
            }

            Text(
                text = "+ заметка",
                fontSize = 10.sp,
                color = PollenTheme.colors.ink3,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

private fun todayDateLabel(): String {
    // TODO: use actual date formatting with locale
    val now = kotlin.time.Clock.System.now()
    val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dayOfWeek = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "пн"
        DayOfWeek.TUESDAY -> "вт"
        DayOfWeek.WEDNESDAY -> "ср"
        DayOfWeek.THURSDAY -> "чт"
        DayOfWeek.FRIDAY -> "пт"
        DayOfWeek.SATURDAY -> "сб"
        DayOfWeek.SUNDAY -> "вс"
    }
    val month = when (date.month) {
        kotlinx.datetime.Month.JANUARY -> "янв"
        kotlinx.datetime.Month.FEBRUARY -> "фев"
        kotlinx.datetime.Month.MARCH -> "мар"
        kotlinx.datetime.Month.APRIL -> "апр"
        kotlinx.datetime.Month.MAY -> "май"
        kotlinx.datetime.Month.JUNE -> "июн"
        kotlinx.datetime.Month.JULY -> "июл"
        kotlinx.datetime.Month.AUGUST -> "авг"
        kotlinx.datetime.Month.SEPTEMBER -> "сен"
        kotlinx.datetime.Month.OCTOBER -> "окт"
        kotlinx.datetime.Month.NOVEMBER -> "ноя"
        kotlinx.datetime.Month.DECEMBER -> "дек"
    }
    return "$dayOfWeek, ${date.day} $month"
}

// endregion
