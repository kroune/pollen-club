package io.github.kroune.pollen.domain.model

object PhenologyStageRegistry {

    private val STAGES_RU = listOf(
        PhenologyStageDomain(1, "Начало сокодвижения", "Первые признаки пробуждения дерева"),
        PhenologyStageDomain(2, "Набухание почек", "Почки начинают увеличиваться"),
        PhenologyStageDomain(3, "Распускание почек", "Видны зелёные кончики листьев"),
        PhenologyStageDomain(4, "Развёртывание листьев", "Листья раскрываются полностью"),
        PhenologyStageDomain(5, "Начало цветения", "Открыты первые цветки"),
        PhenologyStageDomain(6, "Завершение цветения", "Последние цветки отцветают"),
    )

    private val STAGES_EN = listOf(
        PhenologyStageDomain(1, "Sap flow start", "First signs of tree awakening"),
        PhenologyStageDomain(2, "Bud swelling", "Buds begin to enlarge"),
        PhenologyStageDomain(3, "Bud burst", "Green leaf tips visible"),
        PhenologyStageDomain(4, "Leaf unfolding", "Leaves fully unfurl"),
        PhenologyStageDomain(5, "Early flowering", "First flowers open"),
        PhenologyStageDomain(6, "End of flowering", "Last flowers fading"),
    )

    fun getStages(locale: AppLocale): List<PhenologyStageDomain> =
        if (locale == AppLocale.RU) STAGES_RU else STAGES_EN

    fun getStage(number: Int, locale: AppLocale): PhenologyStageDomain? =
        getStages(locale).firstOrNull { it.number == number }
}
