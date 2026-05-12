package io.github.kroune.pollen.presentation.home

import org.jetbrains.compose.resources.DrawableResource
import polenclub.shared.generated.resources.Res
import polenclub.shared.generated.resources.altenariya_gray
import polenclub.shared.generated.resources.ambrosia_gray
import polenclub.shared.generated.resources.bereza_gray
import polenclub.shared.generated.resources.dub_gray
import polenclub.shared.generated.resources.kladosporum_gray
import polenclub.shared.generated.resources.marevye_gray
import polenclub.shared.generated.resources.olkha_gray
import polenclub.shared.generated.resources.oreshnik_gray
import polenclub.shared.generated.resources.polyn_gray
import polenclub.shared.generated.resources.zlaki_gray

object PollenIconRegistry {

    private val ID_TO_ICON: Map<Int, DrawableResource> = mapOf(
        1 to Res.drawable.bereza_gray,       // Birch
        2 to Res.drawable.dub_gray,          // Oak
        3 to Res.drawable.olkha_gray,        // Alder
        4 to Res.drawable.polyn_gray,        // Mugwort
        5 to Res.drawable.oreshnik_gray,     // Hazel
        6 to Res.drawable.zlaki_gray,        // Grasses
        7 to Res.drawable.marevye_gray,      // Goosefoot
        8 to Res.drawable.ambrosia_gray,     // Ragweed
        11 to Res.drawable.kladosporum_gray, // Cladosporium
        12 to Res.drawable.altenariya_gray,  // Alternaria
    )

    fun iconFor(pollenId: Int): DrawableResource? = ID_TO_ICON[pollenId]
}
