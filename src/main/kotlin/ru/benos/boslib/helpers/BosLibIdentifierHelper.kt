package ru.benos.boslib.helpers

import net.minecraft.resources.Identifier

object BosLibIdentifierHelper {
    val String.identifier: Identifier
        get() = Identifier.parse(this@identifier)
}