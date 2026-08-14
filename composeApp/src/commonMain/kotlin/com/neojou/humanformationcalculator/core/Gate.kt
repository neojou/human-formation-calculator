package com.neojou.humanformationcalculator.core

enum class GateKind {
    XOR,
    AND,
    OR,
    NOT,
}

/**
 * One gate-soldier: two input posts and one output flag.
 * [NOT] uses only [in1].
 */
class LogicGate(val kind: GateKind) {
    var in1: Bit? = null
        private set
    var in2: Bit? = null
        private set
    var out: Bit? = null
        private set

    fun setInput(port: Int, bit: Bit) {
        when (port) {
            0 -> in1 = bit
            1 -> {
                require(kind != GateKind.NOT) { "NOT has no second input" }
                in2 = bit
            }
            else -> error("gate input port must be 0 or 1")
        }
    }

    fun evaluate(): Bit {
        val a = requireNotNull(in1) { "$kind missing in1" }
        val result = when (kind) {
            GateKind.NOT -> Logic.not(a)
            GateKind.XOR -> Logic.xor(a, requireNotNull(in2) { "XOR missing in2" })
            GateKind.AND -> Logic.and(a, requireNotNull(in2) { "AND missing in2" })
            GateKind.OR -> Logic.or(a, requireNotNull(in2) { "OR missing in2" })
        }
        out = result
        return result
    }

    fun snapshot(): GateSnapshot = GateSnapshot(kind, in1, in2, out)

    fun clear() {
        in1 = null
        in2 = null
        out = null
    }
}

object Logic {
    fun xor(a: Bit, b: Bit): Bit = Bit.from(a.isOne != b.isOne)
    fun and(a: Bit, b: Bit): Bit = Bit.from(a.isOne && b.isOne)
    fun or(a: Bit, b: Bit): Bit = Bit.from(a.isOne || b.isOne)
    fun not(a: Bit): Bit = Bit.from(!a.isOne)
}

data class GateSnapshot(
    val kind: GateKind,
    val in1: Bit?,
    val in2: Bit?,
    val out: Bit?,
)
