package com.neojou.humanformationcalculator.core

/**
 * One flag bit. White flag = 0, black flag = 1.
 */
enum class Bit {
    ZERO,
    ONE;

    val isOne: Boolean get() = this == ONE

    val intValue: Int get() = if (isOne) 1 else 0

    companion object {
        fun from(value: Boolean): Bit = if (value) ONE else ZERO

        fun fromInt(value: Int): Bit = if (value != 0) ONE else ZERO
    }
}
