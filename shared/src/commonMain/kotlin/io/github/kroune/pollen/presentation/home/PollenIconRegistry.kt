package io.github.kroune.pollen.presentation.home

import io.github.kroune.pollen.domain.model.PollenDomain
import org.jetbrains.compose.resources.DrawableResource
import polenclub.shared.generated.resources.Res
import polenclub.shared.generated.resources.altenariya_gray
import polenclub.shared.generated.resources.ambrosia_gray
import polenclub.shared.generated.resources.bereza_gray
import polenclub.shared.generated.resources.dub_gray
import polenclub.shared.generated.resources.iva_gray
import polenclub.shared.generated.resources.kladosporum_gray
import polenclub.shared.generated.resources.klen_gray
import polenclub.shared.generated.resources.marevye_gray
import polenclub.shared.generated.resources.olkha_gray
import polenclub.shared.generated.resources.oreshnik_gray
import polenclub.shared.generated.resources.polyn_gray
import polenclub.shared.generated.resources.vyaz_gray
import polenclub.shared.generated.resources.yasen_gray
import polenclub.shared.generated.resources.zlaki_gray

object PollenIconRegistry {

    private val NAME_TO_ICON: Map<String, DrawableResource> = mapOf(
        "birch" to Res.drawable.bereza_gray,
        "oak" to Res.drawable.dub_gray,
        "alder" to Res.drawable.olkha_gray,
        "wormwood" to Res.drawable.polyn_gray,
        "mugwort" to Res.drawable.polyn_gray,
        "hazel" to Res.drawable.oreshnik_gray,
        "grass" to Res.drawable.zlaki_gray,
        "grasses" to Res.drawable.zlaki_gray,
        "goosefoot" to Res.drawable.marevye_gray,
        "chenopodiaceae" to Res.drawable.marevye_gray,
        "ragweed" to Res.drawable.ambrosia_gray,
        "cladosporium" to Res.drawable.kladosporum_gray,
        "alternaria" to Res.drawable.altenariya_gray,
        "willow" to Res.drawable.iva_gray,
        "maple" to Res.drawable.klen_gray,
        "ash" to Res.drawable.yasen_gray,
        "elm" to Res.drawable.vyaz_gray,
    )

    fun iconFor(pollen: PollenDomain): DrawableResource? {
        val key = pollen.nameEng.lowercase().trim()
        return NAME_TO_ICON[key] ?: NAME_TO_ICON.entries.firstOrNull { key.contains(it.key) }?.value
    }
}
