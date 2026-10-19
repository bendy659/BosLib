package ru.benos.boslib.helpers

import kotlin.math.PI
import kotlin.math.roundToInt

object BosLibMath {
    val Number.b: Byte
        get() = this@b.toByte()
    val Number.i: Int
        get() = this@i.toInt()
    val Number.ri: Int
        get() = this@ri.f.roundToInt()
    val Number.s: Short
        get() = this@s.toShort()
    val Number.l: Long
        get() = this@l.toLong()
    val Number.d: Double
        get() = this@d.toDouble()
    val Number.f: Float
        get() = this@f.toFloat()

    val Number.toDegrees: Double
        get() = (PI / 180.0) * this@toDegrees.d
    val Number.toRadian: Double
        get() = (180.0 / PI) * this@toRadian.d

}