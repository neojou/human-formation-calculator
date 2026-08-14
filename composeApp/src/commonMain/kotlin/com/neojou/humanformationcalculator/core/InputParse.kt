package com.neojou.humanformationcalculator.core

/**
 * Parse a nibble (0..15).
 *
 * - `0b` / `0B` prefix → binary
 * - exactly four `0`/`1` characters → binary
 * - otherwise decimal 0..15 (`10` is ten, not binary two)
 */
fun parseNibble(raw: String): Int? {
    val s = raw.trim()
    if (s.isEmpty()) return null

    if (s.startsWith("0b", ignoreCase = true)) {
        val bits = s.substring(2)
        if (bits.isEmpty() || bits.length > 4 || bits.any { it != '0' && it != '1' }) return null
        return bits.toInt(2)
    }

    if (s.length == 4 && s.all { it == '0' || it == '1' }) {
        return s.toInt(2)
    }

    val dec = s.toIntOrNull() ?: return null
    if (dec !in 0..15) return null
    return dec
}
