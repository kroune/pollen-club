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
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.dataOrNull
import io.github.kroune.pollen.presentation.common.CollectEvents
import androidx.compose.ui.tooling.preview.Preview
import io.github.kroune.pollen.presentation.common.CategoriesCardSkeleton
import io.github.kroune.pollen.presentation.common.MedicationListSkeleton
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
        MedicationsScreen(
            state = state,
            onBack = onBack,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onToggleTaken = viewModel::toggleTakenToday,
            onToggleSheetExpanded = viewModel::toggleSheetExpanded,
            onRemoveDose = viewModel::removeTodayDose,
        )
    }
}

@Composable
fun MedicationsScreen(
    state: MedicationsUiState,
    onBack: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onToggleTaken: (Long) -> Unit,
    onToggleSheetExpanded: () -> Unit,
    onRemoveDose: (Long) -> Unit,
) {
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
                    onQueryChanged = onSearchQueryChanged,
                )
                Spacer(Modifier.height(18.dp))

                SectionHeader(stringResource(MR.strings.medications_your_meds))
                Spacer(Modifier.height(8.dp))
                when (val meds = state.recentMeds) {
                    is LoadState.Loading -> MedicationListSkeleton()
                    is LoadState.Failed -> MedicationListSkeleton(count = 2)
                    is LoadState.Loaded -> RecentMedsList(
                        meds = meds.data,
                        onToggleTaken = onToggleTaken,
                    )
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = PollenTheme.colors.line2)
                Spacer(Modifier.height(12.dp))

                SectionHeader(stringResource(MR.strings.medications_categories))
                Spacer(Modifier.height(8.dp))
                when (val cats = state.categories) {
                    is LoadState.Loading -> CategoriesCardSkeleton()
                    is LoadState.Failed -> CategoriesCardSkeleton()
                    is LoadState.Loaded -> CategoriesCard(categories = cats.data)
                }
            }
        }

        if (state.isSheetExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(onClick = onToggleSheetExpanded),
            )
        }

        TodaySheet(
            doses = state.todayDoses,
            todayCount = state.todayCount,
            isExpanded = state.isSheetExpanded,
            onToggleExpand = onToggleSheetExpanded,
            onRemoveDose = onRemoveDose,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
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
                modifier = Modifier.size(22.dp),
                tint = PollenTheme.colors.ink2,
            )
        }
        Text(
            text = stringResource(MR.strings.medications_title),
            fontSize = 20.sp,
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
            modifier = Modifier.size(20.dp),
            tint = PollenTheme.colors.ink3,
        )
        Spacer(Modifier.width(10.dp))
        if (query.isEmpty()) {
            Text(
                stringResource(MR.strings.medications_search),
                fontSize = 14.sp,
                color = PollenTheme.colors.ink3,
            )
        } else {
            Text(
                query,
                fontSize = 14.sp,
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PollenTheme.colors.ink,
            )
            Text(
                text = med.substance,
                fontSize = 12.sp,
                color = PollenTheme.colors.ink3,
                modifier = Modifier.padding(top = 1.dp),
            )
            Text(
                text = stringResource(MR.strings.medications_intakes_format, med.count, med.lastTaken),
                fontSize = 11.sp,
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
                text = if (med.takenToday) stringResource(MR.strings.medications_cancel_intake) else stringResource(MR.strings.medications_take),
                fontSize = 12.sp,
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
            .size(34.dp)
            .clip(CircleShape)
            .background(bg, CircleShape)
            .then(if (!isTaken) Modifier.border(1.dp, borderColor, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (isTaken) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = contentColor,
            )
        } else {
            Text(
                text = "$initial",
                fontSize = 11.sp,
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
                    fontSize = 14.sp,
                    color = PollenTheme.colors.ink,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = PollenTheme.colors.ink3,
                )
            }
        }
    }
}

// region Previews

private val previewMeds = persistentListOf(
    RecentMedUi(1, "Цетрин", "Цетиризин", "вчера", 12, true),
    RecentMedUi(2, "Назонекс", "Мометазон", "3 дня назад", 5, false),
    RecentMedUi(3, "Кромогексал", "Кромоглициевая к-та", "неделю назад", 2, false),
)

private val previewCategories = persistentListOf(
    MedCategoryUi(1, "Антигистаминные"),
    MedCategoryUi(2, "Назальные спреи"),
    MedCategoryUi(3, "Глазные капли"),
)

private val previewDoses = persistentListOf(
    TodayDoseUi(1, "Цетрин", "10 мг", 'Ц'),
    TodayDoseUi(2, "Назонекс", "50 мкг", 'Н'),
)

@Preview
@Composable
private fun PreviewMedicationsRecentMeds() {
    PollenTheme {
        Column(Modifier.padding(16.dp)) {
            SectionHeader(stringResource(MR.strings.medications_your_meds))
            Spacer(Modifier.height(8.dp))
            RecentMedsList(meds = previewMeds, onToggleTaken = {})
        }
    }
}

@Preview
@Composable
private fun PreviewMedicationsCategories() {
    PollenTheme {
        Column(Modifier.padding(16.dp)) {
            SectionHeader(stringResource(MR.strings.medications_categories))
            Spacer(Modifier.height(8.dp))
            CategoriesCard(categories = previewCategories)
        }
    }
}

@Preview
@Composable
private fun PreviewMedicationsTodaySheetCollapsed() {
    PollenTheme {
        TodaySheet(
            doses = previewDoses,
            todayCount = 2,
            isExpanded = false,
            onToggleExpand = {},
            onRemoveDose = {},
        )
    }
}

@Preview
@Composable
private fun PreviewMedicationsTodaySheetExpanded() {
    PollenTheme {
        TodaySheet(
            doses = previewDoses,
            todayCount = 2,
            isExpanded = true,
            onToggleExpand = {},
            onRemoveDose = {},
        )
    }
}

@Preview
@Composable
private fun PreviewMedicationsFull() {
    PollenTheme {
        MedicationsScreen(
            state = MedicationsUiState(
                recentMeds = LoadState.Loaded(previewMeds),
                categories = LoadState.Loaded(previewCategories),
                todayDoses = previewDoses,
                todayCount = previewDoses.size,
                searchQuery = "",
                isSheetExpanded = false,
            ),
            onBack = {},
            onSearchQueryChanged = {},
            onToggleTaken = {},
            onToggleSheetExpanded = {},
            onRemoveDose = {},
        )
    }
}

// endregion

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
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PollenTheme.colors.accent, CircleShape)
                        .border(2.dp, PollenTheme.colors.card, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${dose.initial}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        Row(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(MR.strings.date_today).replaceFirstChar { it.uppercase() },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PollenTheme.colors.ink,
            )
            Text(
                text = stringResource(MR.strings.medications_intakes_today, todayCount),
                fontSize = 13.sp,
                color = PollenTheme.colors.ink3,
            )
        }

        Text(
            text = stringResource(MR.strings.medications_details),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.accent2,
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
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
                text = stringResource(MR.strings.date_today).replaceFirstChar { it.uppercase() },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PollenTheme.colors.ink,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = todayDateLabel(),
                fontSize = 13.sp,
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
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PollenTheme.colors.accent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${dose.initial}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = dose.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PollenTheme.colors.ink,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = dose.dosage,
                    fontSize = 13.sp,
                    color = PollenTheme.colors.ink3,
                )

                Spacer(Modifier.width(6.dp))

                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(onClick = onRemove),
                    tint = PollenTheme.colors.ink3,
                )
            }

            Text(
                text = stringResource(MR.strings.medications_add_note),
                fontSize = 12.sp,
                color = PollenTheme.colors.ink3,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun todayDateLabel(): String {
    val now = kotlin.time.Clock.System.now()
    val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dayOfWeek = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> stringResource(MR.strings.dow_mon)
        DayOfWeek.TUESDAY -> stringResource(MR.strings.dow_tue)
        DayOfWeek.WEDNESDAY -> stringResource(MR.strings.dow_wed)
        DayOfWeek.THURSDAY -> stringResource(MR.strings.dow_thu)
        DayOfWeek.FRIDAY -> stringResource(MR.strings.dow_fri)
        DayOfWeek.SATURDAY -> stringResource(MR.strings.dow_sat)
        DayOfWeek.SUNDAY -> stringResource(MR.strings.dow_sun)
    }
    val month = when (date.month) {
        kotlinx.datetime.Month.JANUARY -> stringResource(MR.strings.month_jan_short)
        kotlinx.datetime.Month.FEBRUARY -> stringResource(MR.strings.month_feb_short)
        kotlinx.datetime.Month.MARCH -> stringResource(MR.strings.month_mar_short)
        kotlinx.datetime.Month.APRIL -> stringResource(MR.strings.month_apr_short)
        kotlinx.datetime.Month.MAY -> stringResource(MR.strings.month_may_short)
        kotlinx.datetime.Month.JUNE -> stringResource(MR.strings.month_jun_short)
        kotlinx.datetime.Month.JULY -> stringResource(MR.strings.month_jul_short)
        kotlinx.datetime.Month.AUGUST -> stringResource(MR.strings.month_aug_short)
        kotlinx.datetime.Month.SEPTEMBER -> stringResource(MR.strings.month_sep_short)
        kotlinx.datetime.Month.OCTOBER -> stringResource(MR.strings.month_oct_short)
        kotlinx.datetime.Month.NOVEMBER -> stringResource(MR.strings.month_nov_short)
        kotlinx.datetime.Month.DECEMBER -> stringResource(MR.strings.month_dec_short)
    }
    return "$dayOfWeek, ${date.dayOfMonth} $month"
}

// endregion
