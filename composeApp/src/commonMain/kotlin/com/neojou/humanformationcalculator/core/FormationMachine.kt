package com.neojou.humanformationcalculator.core

import com.neojou.humanformationcalculator.core.programs.buildAdd4BitProgram

enum class MachinePhase {
    Idle,
    Loaded,
    Running,
    Halted,
}

data class MachineSnapshot(
    val a: List<Bit>,
    val b: List<Bit>,
    val sum: List<Bit>,
    val aValue: Int,
    val bValue: Int,
    val sumValue: Int,
    val cin: Bit,
    val cout: Bit,
    val temp1: Bit,
    val carry1: Bit,
    val carry2: Bit,
    val xor: GateSnapshot,
    val and: GateSnapshot,
    val or: GateSnapshot,
    val not: GateSnapshot,
    val pc: Int,
    val highlightIndex: Int,
    val program: List<String>,
    val phase: MachinePhase,
    val lastMessage: String,
    val activeGate: GateKind?,
)

/**
 * Software von Neumann machine: one micro-op per [step].
 */
class FormationMachine {
    val a = Register("A", 4)
    val b = Register("B", 4)
    val sum = Register("Sum", 4)

    var cin: Bit = Bit.ZERO
        private set
    var cout: Bit = Bit.ZERO
        private set
    var temp1: Bit = Bit.ZERO
        private set
    var carry1: Bit = Bit.ZERO
        private set
    var carry2: Bit = Bit.ZERO
        private set

    val xor = LogicGate(GateKind.XOR)
    val and = LogicGate(GateKind.AND)
    val or = LogicGate(GateKind.OR)
    val not = LogicGate(GateKind.NOT)

    var program: List<MicroOp> = emptyList()
        private set
    var pc: Int = 0
        private set
    var phase: MachinePhase = MachinePhase.Idle
        private set
    var lastMessage: String = ""
        private set
    var activeGate: GateKind? = null
        private set

    fun load(aValue: Int, bValue: Int) {
        require(aValue in 0..15 && bValue in 0..15)
        resetFields()
        a.load(aValue)
        b.load(bValue)
        program = buildAdd4BitProgram()
        pc = 0
        phase = MachinePhase.Loaded
        lastMessage = "載入 A=$aValue  B=$bValue"
        activeGate = null
    }

    /**
     * Execute one micro-op. Returns false when nothing more can run.
     */
    fun step(): Boolean {
        if (phase == MachinePhase.Idle || program.isEmpty()) return false
        if (phase == MachinePhase.Halted) return false
        if (pc !in program.indices) {
            phase = MachinePhase.Halted
            return false
        }

        val op = program[pc]
        when (op) {
            is MicroOp.Move -> {
                write(op.to, read(op.from))
                activeGate = gateKindOf(op.to) ?: gateKindOf(op.from)
                pc += 1
                phase = MachinePhase.Running
                lastMessage = op.label
            }
            is MicroOp.Eval -> {
                val result = gate(op.kind).evaluate()
                write(op.dest, result)
                activeGate = op.kind
                pc += 1
                phase = MachinePhase.Running
                lastMessage = op.label
            }
            is MicroOp.Halt -> {
                activeGate = null
                phase = MachinePhase.Halted
                lastMessage = "完成  ${a.toInt()} + ${b.toInt()} = ${sum.toInt()}" +
                    if (cout.isOne) "  （Cout=1）" else ""
                return false
            }
        }
        return true
    }

    fun runToHalt(maxSteps: Int = 256) {
        var n = 0
        while (n < maxSteps && step()) {
            n += 1
        }
    }

    fun reset() {
        resetFields()
        program = emptyList()
        phase = MachinePhase.Idle
        lastMessage = ""
        activeGate = null
    }

    fun snapshot(): MachineSnapshot {
        val highlight = when (phase) {
            MachinePhase.Idle -> -1
            MachinePhase.Loaded -> 0
            MachinePhase.Running -> (pc - 1).coerceAtLeast(0)
            MachinePhase.Halted -> program.indexOfFirst { it is MicroOp.Halt }.takeIf { it >= 0 } ?: (pc)
        }
        return MachineSnapshot(
            a = a.toList(),
            b = b.toList(),
            sum = sum.toList(),
            aValue = a.toInt(),
            bValue = b.toInt(),
            sumValue = sum.toInt(),
            cin = cin,
            cout = cout,
            temp1 = temp1,
            carry1 = carry1,
            carry2 = carry2,
            xor = xor.snapshot(),
            and = and.snapshot(),
            or = or.snapshot(),
            not = not.snapshot(),
            pc = pc,
            highlightIndex = highlight,
            program = program.map { it.label },
            phase = phase,
            lastMessage = lastMessage,
            activeGate = activeGate,
        )
    }

    private fun resetFields() {
        a.clear()
        b.clear()
        sum.clear()
        cin = Bit.ZERO
        cout = Bit.ZERO
        temp1 = Bit.ZERO
        carry1 = Bit.ZERO
        carry2 = Bit.ZERO
        xor.clear()
        and.clear()
        or.clear()
        not.clear()
        pc = 0
    }

    private fun gate(kind: GateKind): LogicGate = when (kind) {
        GateKind.XOR -> xor
        GateKind.AND -> and
        GateKind.OR -> or
        GateKind.NOT -> not
    }

    private fun register(id: RegId): Register = when (id) {
        RegId.A -> a
        RegId.B -> b
        RegId.SUM -> sum
    }

    private fun read(loc: BitLoc): Bit = when (loc) {
        is BitLoc.RegBit -> register(loc.reg).get(loc.index)
        BitLoc.Cin -> cin
        BitLoc.Cout -> cout
        BitLoc.Temp1 -> temp1
        BitLoc.Carry1 -> carry1
        BitLoc.Carry2 -> carry2
        is BitLoc.GateIn -> {
            val g = gate(loc.kind)
            val bit = if (loc.port == 0) g.in1 else g.in2
            requireNotNull(bit) { "read empty ${loc.describe()}" }
        }
        is BitLoc.GateOut -> requireNotNull(gate(loc.kind).out) { "read empty ${loc.describe()}" }
    }

    private fun write(loc: BitLoc, bit: Bit) {
        when (loc) {
            is BitLoc.RegBit -> register(loc.reg).set(loc.index, bit)
            BitLoc.Cin -> cin = bit
            BitLoc.Cout -> cout = bit
            BitLoc.Temp1 -> temp1 = bit
            BitLoc.Carry1 -> carry1 = bit
            BitLoc.Carry2 -> carry2 = bit
            is BitLoc.GateIn -> gate(loc.kind).setInput(loc.port, bit)
            is BitLoc.GateOut -> error("cannot write gate output directly")
        }
    }

    private fun gateKindOf(loc: BitLoc): GateKind? = when (loc) {
        is BitLoc.GateIn -> loc.kind
        is BitLoc.GateOut -> loc.kind
        else -> null
    }
}
