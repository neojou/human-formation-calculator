package com.neojou.humanformationcalculator.core

data class AdderState(
    var a: Bit = Bit.ZERO,
    var b: Bit = Bit.ZERO,
    var cin: Bit = Bit.ZERO,
    var temp1: Bit = Bit.ZERO,
    var carry1: Bit = Bit.ZERO,
    var carry2: Bit = Bit.ZERO,
    var sum: Bit = Bit.ZERO,
    var cout: Bit = Bit.ZERO,
)

private class CavalryUnit(
    val kind: CavalryKind,
    val bitIndex: Int,
    var flag: Bit,
    var path: List<Vec2>,
    var pathIndex: Int,
    var riding: Boolean,
    var lastDelivered: Bit = Bit.ZERO,
) {
    val x: Float get() = path[pathIndex.coerceIn(path.indices)].x
    val y: Float get() = path[pathIndex.coerceIn(path.indices)].y
}

/**
 * Spatial human-formation adder: one [step] is one tick for every soldier and cavalry.
 */
class FormationMachine {
    val a = Register("A", 4)
    val b = Register("B", 4)
    val sum = Register("Sum", 4)
    val adders = Array(4) { AdderState() }

    var phase: MachinePhase = MachinePhase.Idle
        private set
    var lastMessage: String = ""
        private set
    var tick: Int = 0
        private set

    val highCout: Bit get() = adders[3].cout

    private val cavalry = mutableListOf<CavalryUnit>()
    private val deliveredA = BooleanArray(4)
    private val deliveredB = BooleanArray(4)
    private val changedIds = mutableSetOf<String>()

    fun load(aValue: Int, bValue: Int) {
        require(aValue in 0..15 && bValue in 0..15)
        resetFields()
        a.load(aValue)
        b.load(bValue)
        for (i in 0..3) {
            cavalry += CavalryUnit(
                kind = CavalryKind.FETCH_A,
                bitIndex = i,
                flag = a.get(i),
                path = FieldLayout.fetchAPath(i),
                pathIndex = 0,
                riding = true,
            )
            cavalry += CavalryUnit(
                kind = CavalryKind.FETCH_B,
                bitIndex = i,
                flag = b.get(i),
                path = FieldLayout.fetchBPath(i),
                pathIndex = 0,
                riding = true,
            )
            cavalry += CavalryUnit(
                kind = CavalryKind.WRITE_SUM,
                bitIndex = i,
                flag = Bit.ZERO,
                path = FieldLayout.writeSumPath(i),
                pathIndex = 0,
                riding = false,
                lastDelivered = Bit.ZERO,
            )
        }
        phase = MachinePhase.Loaded
        lastMessage = "載入 A=$aValue  B=$bValue。全場白旗，騎兵準備送 A／B。"
    }

    /**
     * One tick. Returns false when the formation is idle or already finished.
     */
    fun step(): Boolean {
        if (phase == MachinePhase.Idle || phase == MachinePhase.Halted) return false
        if (isQuiet()) {
            finish()
            return false
        }
        tickOnce()
        phase = MachinePhase.Running
        lastMessage = "tick $tick"
        if (isQuiet()) {
            finish()
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
        phase = MachinePhase.Idle
        lastMessage = ""
    }

    fun snapshot(): MachineSnapshot {
        val soldiers = mutableListOf<SoldierView>()
        val groups = mutableListOf<GateGroupView>()
        for (i in 0..3) {
            val ad = adders[i]
            soldiers += soldier("dataA$i", "A[$i]", a.get(i), FieldLayout.dataA(i), SoldierRole.DATA)
            soldiers += soldier("dataB$i", "B[$i]", b.get(i), FieldLayout.dataB(i), SoldierRole.DATA)
            soldiers += soldier("dataS$i", "S[$i]", sum.get(i), FieldLayout.dataSum(i), SoldierRole.DATA)
            soldiers += soldier("a$i", "A", ad.a, FieldLayout.adderA(i), SoldierRole.INPUT)
            soldiers += soldier("b$i", "B", ad.b, FieldLayout.adderB(i), SoldierRole.INPUT)
            soldiers += soldier("cin$i", "Cin", ad.cin, FieldLayout.cin(i), SoldierRole.INPUT)
            soldiers += soldier("t1$i", "Temp1", ad.temp1, FieldLayout.temp1(i), SoldierRole.XOR)
            soldiers += soldier("c1$i", "Carry1", ad.carry1, FieldLayout.carry1(i), SoldierRole.AND)
            soldiers += soldier("c2$i", "Carry2", ad.carry2, FieldLayout.carry2(i), SoldierRole.AND)
            soldiers += soldier("cout$i", "Cout", ad.cout, FieldLayout.cout(i), SoldierRole.OR)
            soldiers += soldier("sum$i", "Sum", ad.sum, FieldLayout.adderSum(i), SoldierRole.XOR)
            groups += group(GateKind.XOR, i, "XOR", FieldLayout.adderA(i), FieldLayout.adderB(i), FieldLayout.temp1(i))
            groups += group(GateKind.AND, i, "AND", FieldLayout.adderA(i), FieldLayout.adderB(i), FieldLayout.carry1(i))
            groups += group(GateKind.XOR, i, "XOR", FieldLayout.temp1(i), FieldLayout.cin(i), FieldLayout.adderSum(i))
            groups += group(GateKind.AND, i, "AND", FieldLayout.temp1(i), FieldLayout.cin(i), FieldLayout.carry2(i))
            groups += group(GateKind.OR, i, "OR", FieldLayout.carry1(i), FieldLayout.carry2(i), FieldLayout.cout(i))
        }
        val cavViews = cavalry.map { c ->
            CavalryView(
                id = "${c.kind.name}${c.bitIndex}",
                kind = c.kind,
                bitIndex = c.bitIndex,
                flag = c.flag,
                x = c.x,
                y = c.y,
                riding = c.riding,
                label = when (c.kind) {
                    CavalryKind.FETCH_A -> "騎A"
                    CavalryKind.FETCH_B -> "騎B"
                    CavalryKind.WRITE_SUM -> "騎S"
                },
            )
        }.filter { it.riding || it.kind == CavalryKind.WRITE_SUM }
        return MachineSnapshot(
            soldiers = soldiers,
            cavalry = cavViews,
            groups = groups,
            dataA = a.toList(),
            dataB = b.toList(),
            dataSum = sum.toList(),
            aValue = a.toInt(),
            bValue = b.toInt(),
            sumValue = sum.toInt(),
            highCout = highCout,
            tick = tick,
            phase = phase,
            lastMessage = lastMessage,
        )
    }

    private fun tickOnce() {
        val prev = Array(4) { i -> adders[i].copy() }
        changedIds.clear()

        for (i in 0..3) {
            val p = prev[i]
            val nextCin = if (i == 0) Bit.ZERO else prev[i - 1].cout
            val nextTemp1 = Logic.xor(p.a, p.b)
            val nextCarry1 = Logic.and(p.a, p.b)
            val nextSum = Logic.xor(p.temp1, p.cin)
            val nextCarry2 = Logic.and(p.temp1, p.cin)
            val nextCout = Logic.or(p.carry1, p.carry2)
            val ad = adders[i]
            noteChange("a$i", ad.a, p.a)
            noteChange("b$i", ad.b, p.b)
            if (ad.cin != nextCin) changedIds += "cin$i"
            if (ad.temp1 != nextTemp1) changedIds += "t1$i"
            if (ad.carry1 != nextCarry1) changedIds += "c1$i"
            if (ad.carry2 != nextCarry2) changedIds += "c2$i"
            if (ad.sum != nextSum) changedIds += "sum$i"
            if (ad.cout != nextCout) changedIds += "cout$i"
            ad.cin = nextCin
            ad.temp1 = nextTemp1
            ad.carry1 = nextCarry1
            ad.carry2 = nextCarry2
            ad.sum = nextSum
            ad.cout = nextCout
        }

        for (c in cavalry) {
            if (c.kind == CavalryKind.WRITE_SUM && !c.riding && adders[c.bitIndex].sum != c.lastDelivered) {
                c.flag = adders[c.bitIndex].sum
                c.path = FieldLayout.writeSumPath(c.bitIndex)
                c.pathIndex = 0
                c.riding = true
            }
            if (c.riding) {
                advanceCavalry(c)
            }
        }
        tick += 1
    }

    private fun advanceCavalry(c: CavalryUnit) {
        if (c.pathIndex >= c.path.lastIndex) {
            c.riding = false
            return
        }
        c.pathIndex += 1
        val atEnd = c.pathIndex == c.path.lastIndex
        val atWrite = c.kind == CavalryKind.WRITE_SUM && c.pathIndex == 2
        when (c.kind) {
            CavalryKind.FETCH_A -> if (atEnd) {
                adders[c.bitIndex].a = c.flag
                deliveredA[c.bitIndex] = true
                changedIds += "a${c.bitIndex}"
                c.riding = false
            }
            CavalryKind.FETCH_B -> if (atEnd) {
                adders[c.bitIndex].b = c.flag
                deliveredB[c.bitIndex] = true
                changedIds += "b${c.bitIndex}"
                c.riding = false
            }
            CavalryKind.WRITE_SUM -> {
                if (atWrite) {
                    val before = sum.get(c.bitIndex)
                    sum.set(c.bitIndex, c.flag)
                    c.lastDelivered = c.flag
                    if (before != c.flag) changedIds += "dataS${c.bitIndex}"
                }
                if (atEnd) c.riding = false
            }
        }
    }

    private fun isQuiet(): Boolean {
        if (cavalry.any { it.riding }) return false
        if (deliveredA.any { !it } || deliveredB.any { !it }) return false
        if (cavalry.filter { it.kind == CavalryKind.WRITE_SUM }
                .any { it.lastDelivered != adders[it.bitIndex].sum }
        ) {
            return false
        }
        return !wouldComputeChange()
    }

    private fun wouldComputeChange(): Boolean {
        for (i in 0..3) {
            val ad = adders[i]
            val nextCin = if (i == 0) Bit.ZERO else adders[i - 1].cout
            if (nextCin != ad.cin) return true
            if (Logic.xor(ad.a, ad.b) != ad.temp1) return true
            if (Logic.and(ad.a, ad.b) != ad.carry1) return true
            if (Logic.xor(ad.temp1, ad.cin) != ad.sum) return true
            if (Logic.and(ad.temp1, ad.cin) != ad.carry2) return true
            if (Logic.or(ad.carry1, ad.carry2) != ad.cout) return true
        }
        return false
    }

    private fun finish() {
        phase = MachinePhase.Halted
        val carry = if (highCout.isOne) "  （Cout=1）" else ""
        lastMessage = "完成  ${a.toInt()} + ${b.toInt()} = ${sum.toInt()}$carry  ·  $tick ticks"
    }

    private fun resetFields() {
        a.clear()
        b.clear()
        sum.clear()
        for (i in 0..3) {
            adders[i] = AdderState()
            deliveredA[i] = false
            deliveredB[i] = false
        }
        cavalry.clear()
        changedIds.clear()
        tick = 0
    }

    private fun noteChange(id: String, now: Bit, then: Bit) {
        if (now != then) changedIds += id
    }

    private fun soldier(id: String, label: String, bit: Bit, pos: Vec2, role: SoldierRole) =
        SoldierView(id, label, bit, pos.x, pos.y, changed = id in changedIds, role = role)

    private fun group(kind: GateKind, bit: Int, label: String, vararg pts: Vec2): GateGroupView {
        val minX = pts.minOf { it.x }
        val maxX = pts.maxOf { it.x }
        val minY = pts.minOf { it.y }
        val maxY = pts.maxOf { it.y }
        val padX = 0.028f
        val padY = 0.045f
        return GateGroupView(
            kind = kind,
            bitIndex = bit,
            cx = (minX + maxX) / 2f,
            cy = (minY + maxY) / 2f,
            rx = (maxX - minX) / 2f + padX,
            ry = (maxY - minY) / 2f + padY,
            label = label,
        )
    }
}
