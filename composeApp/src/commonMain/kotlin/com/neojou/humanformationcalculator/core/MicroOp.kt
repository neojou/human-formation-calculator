package com.neojou.humanformationcalculator.core

sealed class MicroOp {
    abstract val label: String

    data class Move(
        val from: BitLoc,
        val to: BitLoc,
        override val label: String,
    ) : MicroOp()

    data class Eval(
        val kind: GateKind,
        val dest: BitLoc,
        override val label: String,
    ) : MicroOp()

    data object Halt : MicroOp() {
        override val label: String = "HALT"
    }
}
