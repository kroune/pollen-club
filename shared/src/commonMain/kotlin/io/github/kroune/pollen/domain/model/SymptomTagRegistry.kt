package io.github.kroune.pollen.domain.model

object SymptomTagRegistry {

    private val ALL_TAGS_RU = listOf(
        SymptomTagDomain("eyes_itch", BodyZone.EYES, "Зуд"),
        SymptomTagDomain("eyes_tears", BodyZone.EYES, "Слезотечение"),
        SymptomTagDomain("eyes_redness", BodyZone.EYES, "Покраснение"),
        SymptomTagDomain("nose_itch", BodyZone.NOSE, "Зуд"),
        SymptomTagDomain("nose_congestion", BodyZone.NOSE, "Заложенность"),
        SymptomTagDomain("nose_rhinorrhea", BodyZone.NOSE, "Ринорея"),
        SymptomTagDomain("nose_sneezing", BodyZone.NOSE, "Чихание"),
        SymptomTagDomain("nose_bleeding", BodyZone.NOSE, "Кровотечения"),
        SymptomTagDomain("throat_itch", BodyZone.THROAT, "Першение"),
        SymptomTagDomain("throat_cough", BodyZone.THROAT, "Кашель"),
        SymptomTagDomain("chest_wheeze", BodyZone.CHEST, "Хрипы"),
        SymptomTagDomain("chest_tightness", BodyZone.CHEST, "Сдавленность"),
        SymptomTagDomain("chest_shortness", BodyZone.CHEST, "Одышка"),
        SymptomTagDomain("skin_rash", BodyZone.SKIN, "Сыпь"),
        SymptomTagDomain("skin_itch", BodyZone.SKIN, "Зуд"),
        SymptomTagDomain("skin_hives", BodyZone.SKIN, "Крапивница"),
    )

    private val ALL_TAGS_EN = listOf(
        SymptomTagDomain("eyes_itch", BodyZone.EYES, "Itching"),
        SymptomTagDomain("eyes_tears", BodyZone.EYES, "Tearing"),
        SymptomTagDomain("eyes_redness", BodyZone.EYES, "Redness"),
        SymptomTagDomain("nose_itch", BodyZone.NOSE, "Itching"),
        SymptomTagDomain("nose_congestion", BodyZone.NOSE, "Congestion"),
        SymptomTagDomain("nose_rhinorrhea", BodyZone.NOSE, "Rhinorrhea"),
        SymptomTagDomain("nose_sneezing", BodyZone.NOSE, "Sneezing"),
        SymptomTagDomain("nose_bleeding", BodyZone.NOSE, "Nosebleeds"),
        SymptomTagDomain("throat_itch", BodyZone.THROAT, "Tickling"),
        SymptomTagDomain("throat_cough", BodyZone.THROAT, "Cough"),
        SymptomTagDomain("chest_wheeze", BodyZone.CHEST, "Wheezing"),
        SymptomTagDomain("chest_tightness", BodyZone.CHEST, "Tightness"),
        SymptomTagDomain("chest_shortness", BodyZone.CHEST, "Shortness of breath"),
        SymptomTagDomain("skin_rash", BodyZone.SKIN, "Rash"),
        SymptomTagDomain("skin_itch", BodyZone.SKIN, "Itching"),
        SymptomTagDomain("skin_hives", BodyZone.SKIN, "Hives"),
    )

    fun getAllTags(locale: AppLocale): List<SymptomTagDomain> = when (locale) {
        AppLocale.RU -> ALL_TAGS_RU
        AppLocale.EN -> ALL_TAGS_EN
    }

    fun getTagsByZone(zone: BodyZone, locale: AppLocale): List<SymptomTagDomain> =
        getAllTags(locale).filter { it.zone == zone }

    fun deriveZoneSeverity(selectedTagKeys: List<String>, zone: BodyZone): Int {
        val zonePrefix = when (zone) {
            BodyZone.EYES -> "eyes_"
            BodyZone.NOSE -> "nose_"
            BodyZone.THROAT -> "throat_"
            BodyZone.CHEST -> "chest_"
            BodyZone.SKIN -> "skin_"
        }
        return selectedTagKeys.count { it.startsWith(zonePrefix) }.coerceAtMost(3)
    }
}
