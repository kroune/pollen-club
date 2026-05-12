package io.github.kroune.pollen.util

import dev.icerock.moko.resources.desc.StringDesc
import io.github.kroune.pollen.domain.model.AppLocale

fun applyMokoLocale(locale: AppLocale) {
    val tag = when (locale) {
        AppLocale.RU -> "ru"
        AppLocale.EN -> "en"
    }
    StringDesc.localeType = StringDesc.LocaleType.Custom(tag)
}
