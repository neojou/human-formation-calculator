package com.neojou.humanformationcalculator.core

data class Vec2(val x: Float, val y: Float) {
    fun mid(other: Vec2): Vec2 = Vec2((x + other.x) / 2f, (y + other.y) / 2f)
}

/**
 * Shared field coordinates. x: left=bit3 … right=bit0. y: 0 top … 1 bottom.
 */
object FieldLayout {
    fun colLeft(bit: Int): Float = (3 - bit) / 4f

    private fun local(bit: Int, lx: Float, ly: Float): Vec2 {
        val x = colLeft(bit) + lx * 0.25f
        val y = 0.18f + ly * 0.64f
        return Vec2(x, y)
    }

    /**
     * Top data: A[3..0] clustered on the left, B[3..0] on the right
     * (MSB left, matching how the nibble is read).
     */
    fun dataA(bit: Int) = Vec2(0.055f + (3 - bit) * 0.088f, 0.068f)

    fun dataB(bit: Int) = Vec2(0.605f + (3 - bit) * 0.088f, 0.068f)

    fun dataSum(bit: Int) = Vec2(colLeft(bit) + 0.155f, 0.935f)

    fun adderA(bit: Int) = local(bit, 0.34f, 0.10f)
    fun adderB(bit: Int) = local(bit, 0.66f, 0.10f)
    fun carry1(bit: Int) = local(bit, 0.20f, 0.38f)
    fun temp1(bit: Int) = local(bit, 0.58f, 0.38f)
    fun cin(bit: Int) = local(bit, 0.88f, 0.42f)
    fun cout(bit: Int) = local(bit, 0.10f, 0.66f)
    fun carry2(bit: Int) = local(bit, 0.40f, 0.66f)
    fun adderSum(bit: Int) = local(bit, 0.62f, 0.88f)

    /**
     * A riders drop, then slide on the upper lane (y≈0.12) into the adder column.
     * B riders use a slightly lower lane (y≈0.16) so crossing traffic does not
     * share a waypoint. Each rider still only writes [adderA]/[adderB] of its bit.
     */
    fun fetchAPath(bit: Int): List<Vec2> {
        val from = dataA(bit)
        val to = adderA(bit)
        val drop = Vec2(from.x, 0.118f)
        val approach = Vec2(to.x, 0.128f)
        return listOf(from, drop, approach, to)
    }

    fun fetchBPath(bit: Int): List<Vec2> {
        val from = dataB(bit)
        val to = adderB(bit)
        val drop = Vec2(from.x, 0.155f)
        val approach = Vec2(to.x, 0.165f)
        return listOf(from, drop, approach, to)
    }

    fun writeSumPath(bit: Int): List<Vec2> {
        val from = adderSum(bit)
        val to = dataSum(bit)
        val mid = Vec2(from.x, (from.y + to.y) / 2f)
        return listOf(from, mid, to, mid, from)
    }
}
