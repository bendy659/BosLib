package ru.benos.boslib.helpers

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

object BosLibComponentHelper {
    val String.literal: MutableComponent
        get() = Component.literal(this)
    val String.translatable: MutableComponent
        get() = Component.translatable(this)

    fun String.literal(vararg args: Any): MutableComponent {
        val label = this@literal.format(*args)
        return Component.literal(label)
    }

    fun String.translatable(vararg args: Any): MutableComponent =
        Component.translatable(this@translatable, *args)
}