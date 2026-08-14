package com.neojou.humanformationcalculator.core

enum class GateKind {
    XOR,
    AND,
    OR,
    NOT,
}

object Logic {
    fun xor(a: Bit, b: Bit): Bit = Bit.from(a.isOne != b.isOne)
    fun and(a: Bit, b: Bit): Bit = Bit.from(a.isOne && b.isOne)
    fun or(a: Bit, b: Bit): Bit = Bit.from(a.isOne || b.isOne)
    fun not(a: Bit): Bit = Bit.from(!a.isOne)
}
