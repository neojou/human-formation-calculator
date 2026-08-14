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

    fun dataA(bit: Int) = Vec2(colLeft(bit) + 0.055f, 0.065f)
    fun dataB(bit: Int) = Vec2(colLeft(bit) + 0.155f, 0.065f)
    fun dataSum(bit: Int) = Vec2(colLeft(bit) + 0.155f, 0.935f)

    fun adderA(bit: Int) = local(bit, 0.40f, 0.12f)
    fun adderB(bit: Int) = local(bit, 0.58f, 0.12f)
    fun carry1(bit: Int) = local(bit, 0.24f, 0.38f)
    fun temp1(bit: Int) = local(bit, 0.62f, 0.38f)
    fun cin(bit: Int) = local(bit, 0.88f, 0.40f)
    fun cout(bit: Int) = local(bit, 0.08f, 0.64f)
    fun carry2(bit: Int) = local(bit, 0.38f, 0.64f)
    fun adderSum(bit: Int) = local(bit, 0.62f, 0.86f)

    fun fetchAPath(bit: Int): List<Vec2> {
        val from = dataA(bit)
        val to = adderA(bit)
        return listOf(from, Vec2(from.x, (from.y + to.y) / 2f), to)
    }

    fun fetchBPath(bit: Int): List<Vec2> {
        val from = dataB(bit)
        val to = adderB(bit)
        return listOf(from, Vec2(from.x, (from.y + to.y) / 2f), to)
    }

    fun writeSumPath(bit: Int): List<Vec2> {
        val from = adderSum(bit)
        val to = dataSum(bit)
        val mid = Vec2(from.x, (from.y + to.y) / 2f)
        return listOf(from, mid, to, mid, from)
    }
}
