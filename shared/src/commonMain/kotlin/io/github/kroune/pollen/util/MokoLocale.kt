package io.github.kroune.pollen.util

import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.domain.model.AppLocale

fun applyMokoLocale(locale: AppLocale) {
    StringDesc.localeType = StringDesc.LocaleType.Custom(locale.tag)
}
