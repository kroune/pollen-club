package io.github.kroune.pollen.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.PollenLevelDomain
import io.github.kroune.pollen.domain.model.WeatherDomain
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private val previewPollen = PollenDomain(
    id = 1,
    name = "Берёза",
    description = "Common allergen in spring",
    maxLevel = 4,
    levels = listOf(
        PollenLevelDomain(level = 0, name = "Нулевой", info = "", color = 0xFFBCC8C0.toInt()),
        PollenLevelDomain(level = 1, name = "Низкий", info = "", color = 0xFF6EA85C.toInt()),
        PollenLevelDomain(level = 2, name = "Средний", info = "", color = 0xFFE5A50A.toInt()),
        PollenLevelDomain(level = 3, name = "Высокий", info = "", color = 0xFFD4713A.toInt()),
        PollenLevelDomain(level = 4, name = "Очень высокий", info = "", color = 0xFFC43D3D.toInt()),
    ),
)

private val previewLevel = LevelDomain(
    id = 1,
    date = "2026-05-05",
    pollenId = 1,
    locationId = 1,
    value = 2,
)

private val previewWeather = WeatherDomain(
    temperature = 22.0,
    weatherCode = 1,
    isDay = true,
)

@Preview
@Composable
private fun PreviewWeatherCard() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            WeatherCard(weather = previewWeather)
        }
    }
}

@Preview
@Composable
private fun PreviewLocationRow() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            LocationRow(locationName = "Москва", onLocationClick = {}, onSettingsClick = {})
        }
    }
}

@Preview
@Composable
private fun PreviewDayStrip() {
    val days = persistentListOf(
        HomeDayForecastUi(23, StringDesc.Raw("ср"), 1, "2026-04-23"),
        HomeDayForecastUi(24, StringDesc.Raw("чт"), 1, "2026-04-24"),
        HomeDayForecastUi(25, StringDesc.Raw("пт"), 3, "2026-04-25"),
        HomeDayForecastUi(26, StringDesc.Raw("сб"), 2, "2026-04-26"),
        HomeDayForecastUi(27, StringDesc.Raw("вс"), 3, "2026-04-27"),
        HomeDayForecastUi(28, StringDesc.Raw("пн"), 2, "2026-04-28"),
        HomeDayForecastUi(29, StringDesc.Raw("вт"), 1, "2026-04-29"),
    )
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            DayStrip(
                days = days,
                activeDayIndex = 3,
                weekLabel = "21–27 Apr",
                onDaySelected = {},
                onPreviousWeek = {},
                onNextWeek = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewPersonalIndexCard() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            PersonalIndexCard(score = "5,2", severityLevel = 2, label = "Средний")
        }
    }
}

@Preview
@Composable
private fun PreviewSeverityDotsRow() {
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            SeverityDotsRow(level = 2)
        }
    }
}

@Preview
@Composable
private fun PreviewAllergenListCard() {
    val allergens = persistentListOf(
        AllergenRowData(previewPollen, 2, 4),
        AllergenRowData(previewPollen.copy(id = 2, name = "Орешник"), 0, 4),
        AllergenRowData(previewPollen.copy(id = 3, name = "Ольха"), 0, 4),
    )
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            AllergenListCard(allergens = allergens, onAllergenClick = {})
        }
    }
}

@Preview
@Composable
private fun PreviewOtherAllergenPills() {
    val others = persistentListOf(
        previewPollen.copy(id = 4, name = "Дуб"),
        previewPollen.copy(id = 5, name = "Полынь"),
        previewPollen.copy(id = 6, name = "Злаки"),
        previewPollen.copy(id = 7, name = "Маревые"),
        previewPollen.copy(id = 8, name = "Амброзия"),
        previewPollen.copy(id = 9, name = "Кладоспориум"),
        previewPollen.copy(id = 10, name = "Альтернария"),
    )
    PollenTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            OtherAllergensSection(allergens = others, onAllergenAdd = {})
        }
    }
}

@Preview
@Composable
private fun PreviewHomeScreenFull() {
    val days = persistentListOf(
        HomeDayForecastUi(23, StringDesc.Raw("ср"), 1, "2026-04-23"),
        HomeDayForecastUi(24, StringDesc.Raw("чт"), 1, "2026-04-24"),
        HomeDayForecastUi(25, StringDesc.Raw("пт"), 3, "2026-04-25"),
        HomeDayForecastUi(26, StringDesc.Raw("сб"), 2, "2026-04-26"),
        HomeDayForecastUi(27, StringDesc.Raw("вс"), 3, "2026-04-27"),
        HomeDayForecastUi(28, StringDesc.Raw("пн"), 2, "2026-04-28"),
        HomeDayForecastUi(29, StringDesc.Raw("вт"), 1, "2026-04-29"),
    )
    val allergens = persistentListOf(
        AllergenRowData(previewPollen, 2, 4),
        AllergenRowData(previewPollen.copy(id = 2, name = "Орешник"), 0, 4),
        AllergenRowData(previewPollen.copy(id = 3, name = "Ольха"), 0, 4),
    )
    val others = persistentListOf(
        previewPollen.copy(id = 4, name = "Дуб"),
        previewPollen.copy(id = 5, name = "Полынь"),
        previewPollen.copy(id = 6, name = "Злаки"),
        previewPollen.copy(id = 7, name = "Маревые"),
        previewPollen.copy(id = 8, name = "Амброзия"),
        previewPollen.copy(id = 9, name = "Кладоспориум"),
        previewPollen.copy(id = 10, name = "Альтернария"),
    )
    PollenTheme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
        ) {
            LocationRow(locationName = "Москва", onLocationClick = {}, onSettingsClick = {})
            DayStrip(
                days = days,
                activeDayIndex = 3,
                weekLabel = "21–27 Apr",
                onDaySelected = {},
                onPreviousWeek = {},
                onNextWeek = {},
            )
            PersonalIndexCard(
                score = "5,2",
                severityLevel = 2,
                label = "Средний",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            SectionHeader(
                title = "ВАШИ АЛЛЕРГЕНЫ",
                actionLabel = "настроить",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            AllergenListCard(
                allergens = allergens,
                onAllergenClick = {},
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            OtherAllergensSection(
                allergens = others,
                onAllergenAdd = {},
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
