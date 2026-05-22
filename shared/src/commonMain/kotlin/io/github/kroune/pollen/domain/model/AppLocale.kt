package io.github.kroune.pollen.domain.model

enum class AppLocale(val tag: String) {
    RU("ru"),
    EN("en");

    companion object {
        val Default: AppLocale = RU
        fun fromTag(tag: String?): AppLocale = entries.firstOrNull { it.tag == tag } ?: Default
    }
}
