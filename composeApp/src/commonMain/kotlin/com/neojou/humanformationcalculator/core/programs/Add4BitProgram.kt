package com.neojou.humanformationcalculator.core.programs

import com.neojou.humanformationcalculator.core.BitLoc
import com.neojou.humanformationcalculator.core.GateKind
import com.neojou.humanformationcalculator.core.MicroOp
import com.neojou.humanformationcalculator.core.RegId
import com.neojou.humanformationcalculator.core.describe

/**
 * Ripple-carry 4-bit adder as a readable micro-program.
 *
 * Each bit i (LSB first):
 *   Temp1  = A[i] XOR B[i]
 *   Carry1 = A[i] AND B[i]
 *   Sum[i] = Temp1 XOR Cin
 *   Carry2 = Temp1 AND Cin
 *   Cout   = Carry1 OR Carry2
 *   Cin    = Cout
 */
fun buildAdd4BitProgram(): List<MicroOp> {
    val ops = mutableListOf<MicroOp>()
    for (i in 0..3) {
        ops += move(BitLoc.RegBit(RegId.A, i), BitLoc.GateIn(GateKind.XOR, 0))
        ops += move(BitLoc.RegBit(RegId.B, i), BitLoc.GateIn(GateKind.XOR, 1))
        ops += eval(GateKind.XOR, BitLoc.Temp1, "A[$i] ⊕ B[$i] → Temp1")

        ops += move(BitLoc.RegBit(RegId.A, i), BitLoc.GateIn(GateKind.AND, 0))
        ops += move(BitLoc.RegBit(RegId.B, i), BitLoc.GateIn(GateKind.AND, 1))
        ops += eval(GateKind.AND, BitLoc.Carry1, "A[$i] ∧ B[$i] → Carry1")

        ops += move(BitLoc.Temp1, BitLoc.GateIn(GateKind.XOR, 0))
        ops += move(BitLoc.Cin, BitLoc.GateIn(GateKind.XOR, 1))
        ops += eval(GateKind.XOR, BitLoc.RegBit(RegId.SUM, i), "Temp1 ⊕ Cin → Sum[$i]")

        ops += move(BitLoc.Temp1, BitLoc.GateIn(GateKind.AND, 0))
        ops += move(BitLoc.Cin, BitLoc.GateIn(GateKind.AND, 1))
        ops += eval(GateKind.AND, BitLoc.Carry2, "Temp1 ∧ Cin → Carry2")

        ops += move(BitLoc.Carry1, BitLoc.GateIn(GateKind.OR, 0))
        ops += move(BitLoc.Carry2, BitLoc.GateIn(GateKind.OR, 1))
        ops += eval(GateKind.OR, BitLoc.Cout, "Carry1 ∨ Carry2 → Cout")

        ops += move(BitLoc.Cout, BitLoc.Cin, extra = "進下一位")
    }
    ops += MicroOp.Halt
    return ops
}

private fun move(from: BitLoc, to: BitLoc, extra: String? = null): MicroOp.Move {
    val suffix = extra?.let { "（$it）" }.orEmpty()
    return MicroOp.Move(from, to, "騎兵：${from.describe()} → ${to.describe()}$suffix")
}

private fun eval(kind: GateKind, dest: BitLoc, formula: String): MicroOp.Eval =
    MicroOp.Eval(kind, dest, "門士兵 $kind：$formula")
