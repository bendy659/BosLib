package ru.benos.boslib

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier

object Helper {
    val String.identifier: Identifier
        get() = Identifier.parse(this@identifier)

    val String.literal: MutableComponent
        get() = Component.literal(this@literal)
    val String.translatable: MutableComponent
        get() = Component.translatable(this@translatable)

    fun String.literal(label: String, vararg args: Any): MutableComponent =
        Component.literal(label.format(*args))
    fun String.translatable(label: String, vararg args: Any): MutableComponent =
        Component.translatable(label.format(*args))
}