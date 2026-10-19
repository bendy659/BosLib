package ru.benos.boslib.helpers

import org.joml.Vector2f
import org.joml.Vector2i
import ru.benos.boslib.helpers.BosLibMath.f
import ru.benos.boslib.helpers.BosLibMath.ri

object BosLibVector2 {
    val Vector2i.toVector2f: Vector2f
        get() = Vector2f(x.f, y.f)
    val Vector2f.toVector2i: Vector2i
        get() = Vector2i(x.ri, y.ri)
}