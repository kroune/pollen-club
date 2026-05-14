package io.github.kroune.pollen.util

fun normalizeSeverity(value: Int, maxLevel: Int): Int {
    if (maxLevel <= 0) return 0
    return ((value.toDouble() / maxLevel) * 5).toInt().coerceIn(0, 5)
}
