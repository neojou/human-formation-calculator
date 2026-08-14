package com.neojou.humanformationcalculator.core

enum class RegId {
    A,
    B,
    SUM,
}

/**
 * Address of one bit the cavalry can pick up or deliver.
 */
sealed class BitLoc {
    data class RegBit(val reg: RegId, val index: Int) : BitLoc()
    data object Cin : BitLoc()
    data object Cout : BitLoc()
    data object Temp1 : BitLoc()
    data object Carry1 : BitLoc()
    data object Carry2 : BitLoc()
    data class GateIn(val kind: GateKind, val port: Int) : BitLoc()
    data class GateOut(val kind: GateKind) : BitLoc()
}

fun BitLoc.describe(): String = when (this) {
    is BitLoc.RegBit -> {
        val name = when (reg) {
            RegId.A -> "A"
            RegId.B -> "B"
            RegId.SUM -> "Sum"
        }
        "$name[$index]"
    }
    BitLoc.Cin -> "Cin"
    BitLoc.Cout -> "Cout"
    BitLoc.Temp1 -> "Temp1"
    BitLoc.Carry1 -> "Carry1"
    BitLoc.Carry2 -> "Carry2"
    is BitLoc.GateIn -> "${kind.name} 入${port + 1}"
    is BitLoc.GateOut -> "${kind.name} 出"
}
