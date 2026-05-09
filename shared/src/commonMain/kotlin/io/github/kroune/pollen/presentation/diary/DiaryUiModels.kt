package io.github.kroune.pollen.presentation.diary

import androidx.compose.runtime.Immutable
import io.github.kroune.pollen.domain.model.BodyZone
import io.github.kroune.pollen.domain.model.Feeling
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DiaryDateUi(
    val dayOfMonth: Int,
    val dayOfWeek: String,
    val isSelected: Boolean,
    val isoDate: String,
)

@Immutable
data class DiaryMoodOptionUi(
    val feeling: Feeling,
    val label: String,
    val isSelected: Boolean,
)

@Immutable
data class DiaryBodyZoneUi(
    val zone: BodyZone,
    val label: String,
    val symptomCount: Int,
    val isSelected: Boolean,
)

@Immutable
data class DiarySymptomTagUi(
    val key: String,
    val label: String,
    val isSelected: Boolean,
)

@Immutable
data class DiaryTherapyItemUi(
    val therapyId: Long,
    val name: String,
    val dosage: String,
    val time: String,
    val taken: Boolean,
)

@Immutable
data class DiaryUiState(
    val monthLabel: String = "",
    val selectedIsoDate: String = "",
    val dates: ImmutableList<DiaryDateUi> = persistentListOf(),
    val moodOptions: ImmutableList<DiaryMoodOptionUi> = persistentListOf(),
    val bodyZones: ImmutableList<DiaryBodyZoneUi> = persistentListOf(),
    val selectedZoneLabel: String? = null,
    val selectedZoneTags: ImmutableList<DiarySymptomTagUi> = persistentListOf(),
    val therapyItems: ImmutableList<DiaryTherapyItemUi> = persistentListOf(),
)
