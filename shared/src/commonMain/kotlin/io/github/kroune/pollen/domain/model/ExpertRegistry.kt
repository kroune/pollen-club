package io.github.kroune.pollen.domain.model

import org.jetbrains.compose.resources.DrawableResource
import polenclub.shared.generated.resources.Res
import polenclub.shared.generated.resources.expert_1
import polenclub.shared.generated.resources.expert_2
import polenclub.shared.generated.resources.expert_3
import polenclub.shared.generated.resources.expert_4
import polenclub.shared.generated.resources.expert_5

data class ExpertProfileDomain(
    val name: ExpertInfoDomain,
    val photo: DrawableResource,
)

object ExpertRegistry {

    private val EXPERTS_RU = mapOf(
        1 to ExpertProfileDomain(ExpertInfoDomain("Андрей", "Мнение эксперта"), Res.drawable.expert_1),
        2 to ExpertProfileDomain(ExpertInfoDomain("Елена", "К.б.н., ведущий научный сотрудник биологического факультета МГУ"), Res.drawable.expert_2),
        3 to ExpertProfileDomain(ExpertInfoDomain("Татьяна", "Д.м.н., ведущий научный сотрудник ФГБУ \"ГНЦ Институт иммунологии\""), Res.drawable.expert_3),
        4 to ExpertProfileDomain(ExpertInfoDomain("Команда", "Пыльца Club"), Res.drawable.expert_4),
        5 to ExpertProfileDomain(ExpertInfoDomain("Пользователи", "Один из пользователей оставил комментарий"), Res.drawable.expert_5),
    )

    private val EXPERTS_EN = mapOf(
        1 to ExpertProfileDomain(ExpertInfoDomain("Andrey", "Expert opinion"), Res.drawable.expert_1),
        2 to ExpertProfileDomain(ExpertInfoDomain("Elena", "Ph.D. in Biology, Lomonosov Moscow State University"), Res.drawable.expert_2),
        3 to ExpertProfileDomain(ExpertInfoDomain("Tatyana", "Doctor of Medical Science, Immunology Institute"), Res.drawable.expert_3),
        4 to ExpertProfileDomain(ExpertInfoDomain("Team", "Pollen Club"), Res.drawable.expert_4),
        5 to ExpertProfileDomain(ExpertInfoDomain("Users", "One user posted comment"), Res.drawable.expert_5),
    )

    private val FALLBACK = ExpertProfileDomain(ExpertInfoDomain("", ""), Res.drawable.expert_1)

    fun get(expertId: Int, locale: AppLocale): ExpertProfileDomain {
        val map = if (locale == AppLocale.RU) EXPERTS_RU else EXPERTS_EN
        return map[expertId] ?: FALLBACK
    }
}
