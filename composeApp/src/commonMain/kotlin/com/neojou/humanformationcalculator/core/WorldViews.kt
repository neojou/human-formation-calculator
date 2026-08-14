package com.neojou.humanformationcalculator.core

enum class MachinePhase {
    Idle,
    Loaded,
    Running,
    Halted,
}

enum class CavalryKind {
    FETCH_A,
    FETCH_B,
    WRITE_SUM,
}

enum class SoldierRole {
    DATA,
    INPUT,
    AND,
    OR,
    XOR,
    ;

    /** Chest mark on operation soldiers. */
    val chestMark: String?
        get() = when (this) {
            AND -> "A"
            OR -> "O"
            XOR -> "X"
            else -> null
        }
}

data class SoldierView(
    val id: String,
    val label: String,
    val bit: Bit,
    val x: Float,
    val y: Float,
    val changed: Boolean,
    val role: SoldierRole,
)

data class CavalryView(
    val id: String,
    val kind: CavalryKind,
    val bitIndex: Int,
    val flag: Bit,
    val x: Float,
    val y: Float,
    val riding: Boolean,
    val label: String,
)

data class GateGroupView(
    val kind: GateKind,
    val bitIndex: Int,
    val cx: Float,
    val cy: Float,
    val rx: Float,
    val ry: Float,
    val label: String,
)

data class MachineSnapshot(
    val soldiers: List<SoldierView>,
    val cavalry: List<CavalryView>,
    val groups: List<GateGroupView>,
    val dataA: List<Bit>,
    val dataB: List<Bit>,
    val dataSum: List<Bit>,
    val aValue: Int,
    val bValue: Int,
    val sumValue: Int,
    val highCout: Bit,
    val tick: Int,
    val phase: MachinePhase,
    val lastMessage: String,
)
