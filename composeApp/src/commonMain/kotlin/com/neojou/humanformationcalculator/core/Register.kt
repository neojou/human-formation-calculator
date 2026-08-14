package com.neojou.humanformationcalculator.core

/**
 * Fixed-width bit register. Index 0 is the least-significant bit.
 */
class Register(val name: String, val width: Int) {
    private val bits = Array(width) { Bit.ZERO }

    fun get(index: Int): Bit {
        require(index in 0 until width) { "$name[$index] out of 0..${width - 1}" }
        return bits[index]
    }

    fun set(index: Int, bit: Bit) {
        require(index in 0 until width) { "$name[$index] out of 0..${width - 1}" }
        bits[index] = bit
    }

    fun load(value: Int) {
        val max = 1 shl width
        require(value in 0 until max) { "$name load $value out of 0..${max - 1}" }
        for (i in 0 until width) {
            bits[i] = Bit.fromInt((value shr i) and 1)
        }
    }

    fun toInt(): Int {
        var acc = 0
        for (i in 0 until width) {
            acc = acc or (bits[i].intValue shl i)
        }
        return acc
    }

    fun toList(): List<Bit> = bits.toList()

    fun clear() {
        bits.fill(Bit.ZERO)
    }
}
