package ru.benos.boslib.client.helpers

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

abstract class BosScreenHelper : Screen {
    constructor(title: Component) : super(title)

    abstract fun render(graphics: GuiGraphicsExtractor)

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) =
        render(graphics)
}