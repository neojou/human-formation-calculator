package com.neojou.humanformationcalculator.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Add4BitTest {

    @Test
    fun sevenPlusFive() {
        val machine = FormationMachine()
        machine.load(7, 5)
        assertEquals(listOf(Bit.ONE, Bit.ONE, Bit.ONE, Bit.ZERO), machine.a.toList())
        assertEquals(listOf(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO), machine.b.toList())
        machine.runToHalt()
        assertEquals(12, machine.sum.toInt())
        assertEquals(Bit.ZERO, machine.cout)
        assertEquals(MachinePhase.Halted, machine.phase)
    }

    @Test
    fun overflowCarry() {
        val machine = FormationMachine()
        machine.load(15, 1)
        machine.runToHalt()
        assertEquals(0, machine.sum.toInt())
        assertEquals(Bit.ONE, machine.cout)
    }

    @Test
    fun allPairsMatchIntegerAdd() {
        val machine = FormationMachine()
        for (a in 0..15) {
            for (b in 0..15) {
                machine.load(a, b)
                machine.runToHalt()
                val total = a + b
                assertEquals(total and 0xF, machine.sum.toInt(), "$a + $b sum")
                assertEquals(if (total >= 16) Bit.ONE else Bit.ZERO, machine.cout, "$a + $b cout")
            }
        }
    }

    @Test
    fun stepThenHaltDoesNotRun() {
        val machine = FormationMachine()
        assertFalse(machine.step())
        machine.load(1, 1)
        assertTrue(machine.step())
        machine.runToHalt()
        assertFalse(machine.step())
    }

    @Test
    fun parseNibbleRules() {
        assertEquals(7, parseNibble("7"))
        assertEquals(10, parseNibble("10"))
        assertEquals(10, parseNibble("1010"))
        assertEquals(5, parseNibble("0b0101"))
        assertEquals(5, parseNibble("0B101"))
        assertNull(parseNibble("16"))
        assertNull(parseNibble("102"))
        assertNull(parseNibble(""))
        assertNull(parseNibble("0b2"))
    }
}
