package ru.benos.boslib.helpers

import com.mojang.renderpearl.api.pipeline.RenderPipeline
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import org.joml.Matrix3x2fStack
import org.joml.Vector2f
import ru.benos.boslib.helpers.BosLibIdentifierHelper.identifier
import ru.benos.boslib.helpers.BosLibMath.f
import ru.benos.boslib.helpers.BosLibMath.ri
import java.awt.Insets

object BosLibGuiGraphicsExtractorHelper {
    class MatrixTransform {
        val pose: Matrix3x2fStack
        val translate: Vector2f?
        val rotate: Float?
        val scale: Vector2f?
        val pivot: Vector2f?

        constructor(pose: Matrix3x2fStack, translate: Vector2f? = null, rotate: Float? = null, scale: Vector2f? = null, pivot: Vector2f? = null) {
            this.pose = pose
            this.translate = translate
            this.rotate = rotate
            this.scale = scale
            this.pivot = pivot
        }

        fun draw(body: () -> Unit) {
            pose.pushMatrix()
            tryTranslate(); tryRotate(); tryScale()
            body()
            pose.popMatrix()
        }

        private fun tryTranslate() {
            val v = translate ?: return
            this.pose.translate(v.x.f, v.y.f)
        }

        private fun tryRotate() {
            val v1 = this.rotate ?: return
            val v2 = this.pivot

            if (v2 != null) {
                this.pose.rotateAbout(v1, v2.x, v2.y)
            } else {
                this.pose.rotate(v1)
            }
        }

        private fun tryScale() {
            val v1 = this.scale ?: return
            val v2 = this.pivot

            if (v2 != null) {
                this.pose.scaleAround(v1.x, v1.y, v2.x, v2.y)
            } else {
                this.pose.scale(v1.x, v1.y)
            }
        }
    }

    class NineSliceTexture {
        val graphics: GuiGraphicsExtractor
        val renderPipeline: RenderPipeline
        val texture: Identifier
        val x: Int
        val y: Int
        val width: Int
        val height: Int
        val textureX: Float
        val textureY: Float
        val textureWidth: Float
        val textureHeight: Float
        val margin: Insets
        val tiling: Boolean
        val textureTotalWidth: Int
        val textureTotalHeight: Int

        constructor(
            graphics: GuiGraphicsExtractor,
            renderPipeline: RenderPipeline,
            texture: Identifier,
            x: Int,
            y: Int,
            width: Int,
            height: Int,
            textureX: Float,
            textureY: Float,
            textureWidth: Float,
            textureHeight: Float,
            margin: Insets,
            tiling: Boolean = false,
            textureTotalWidth: Int = 256,
            textureTotalHeight: Int = 256
        ) {
            this.graphics = graphics
            this.renderPipeline = renderPipeline
            this.texture = texture
            this.x = x
            this.y = y
            this.width = width
            this.height = height
            this.textureX = textureX
            this.textureY = textureY
            this.textureWidth = textureWidth
            this.textureHeight = textureHeight
            this.margin = margin
            this.tiling = tiling
            this.textureTotalWidth = textureTotalWidth
            this.textureTotalHeight = textureTotalHeight
        }

        fun draw() {
            val marginLeft = margin.left.toFloat()
            val marginTop = margin.top.toFloat()
            val marginRight = margin.right.toFloat()
            val marginBottom = margin.bottom.toFloat()

            val totalMarginX = margin.left + margin.right
            val (leftW, rightW) = if (totalMarginX > width && totalMarginX > 0) {
                val l = (width * margin.left) / totalMarginX
                l to (width - l)
            } else {
                margin.left to margin.right
            }
            val middleW = (width - leftW - rightW).coerceAtLeast(0)

            val totalMarginY = margin.top + margin.bottom
            val (topH, bottomH) = if (totalMarginY > height && totalMarginY > 0) {
                val t = (height * margin.top) / totalMarginY
                t to (height - t)
            } else {
                margin.top to margin.bottom
            }
            val middleH = (height - topH - bottomH).coerceAtLeast(0)

            val srcLeftW = minOf(marginLeft, textureWidth)
            val srcRightW = minOf(marginRight, (textureWidth - srcLeftW).coerceAtLeast(0f))
            val srcMidW = (textureWidth - srcLeftW - srcRightW).coerceAtLeast(0f)

            val srcTopH = minOf(marginTop, textureHeight)
            val srcBottomH = minOf(marginBottom, (textureHeight - srcTopH).coerceAtLeast(0f))
            val srcMidH = (textureHeight - srcTopH - srcBottomH).coerceAtLeast(0f)

            val u0 = textureX
            val u1 = textureX + srcLeftW
            val u2 = textureX + textureWidth - srcRightW

            val v0 = textureY
            val v1 = textureY + srcTopH
            val v2 = textureY + textureHeight - srcBottomH

            val dx0 = x
            val dx1 = x + leftW
            val dx2 = x + width - rightW

            val dy0 = y
            val dy1 = y + topH
            val dy2 = y + height - bottomH

            fun drawSegment(
                dstX: Int, dstY: Int, dstW: Int, dstH: Int,
                srcU: Float, srcV: Float, srcW: Float, srcH: Float,
                tileX: Boolean, tileY: Boolean
            ) {
                if (dstW <= 0 || dstH <= 0 || srcW <= 0f || srcH <= 0f) return

                if (!tileX && !tileY) {
                    graphics.blit(
                        renderPipeline,
                        texture,
                        dstX, dstY,
                        srcU, srcV,
                        dstW, dstH,
                        srcW.toInt(), srcH.toInt(),
                        textureTotalWidth, textureTotalHeight
                    )
                    return
                }

                val stepW = if (tileX) srcW.toInt().coerceAtLeast(1) else dstW
                val stepH = if (tileY) srcH.toInt().coerceAtLeast(1) else dstH

                var curY = dstY
                var remH = dstH
                while (remH > 0) {
                    val chunkH = minOf(remH, stepH)
                    val chunkSrcH = if (tileY) chunkH.toFloat() else srcH

                    var curX = dstX
                    var remW = dstW
                    while (remW > 0) {
                        val chunkW = minOf(remW, stepW)
                        val chunkSrcW = if (tileX) chunkW.toFloat() else srcW

                        graphics.blit(
                            RenderPipelines.GUI_TEXTURED,
                            texture,
                            curX, curY,
                            srcU, srcV,
                            chunkW, chunkH,
                            chunkSrcW.toInt(), chunkSrcH.toInt(),
                            textureTotalWidth, textureTotalHeight
                        )
                        curX += chunkW
                        remW -= chunkW
                    }
                    curY += chunkH
                    remH -= chunkH
                }
            }

            // Row 1: Top
            drawSegment(dx0, dy0, leftW, topH, u0, v0, srcLeftW, srcTopH, tileX = false, tileY = false)
            drawSegment(dx1, dy0, middleW, topH, u1, v0, srcMidW, srcTopH, tileX = tiling, tileY = false)
            drawSegment(dx2, dy0, rightW, topH, u2, v0, srcRightW, srcTopH, tileX = false, tileY = false)

            // Row 2: Middle
            drawSegment(dx0, dy1, leftW, middleH, u0, v1, srcLeftW, srcMidH, tileX = false, tileY = tiling)
            drawSegment(dx1, dy1, middleW, middleH, u1, v1, srcMidW, srcMidH, tileX = tiling, tileY = tiling)
            drawSegment(dx2, dy1, rightW, middleH, u2, v1, srcRightW, srcMidH, tileX = false, tileY = tiling)

            // Row 3: Bottom
            drawSegment(dx0, dy2, leftW, bottomH, u0, v2, srcLeftW, srcBottomH, tileX = false, tileY = false)
            drawSegment(dx1, dy2, middleW, bottomH, u1, v2, srcMidW, srcBottomH, tileX = tiling, tileY = false)
            drawSegment(dx2, dy2, rightW, bottomH, u2, v2, srcRightW, srcBottomH, tileX = false, tileY = false)
        }
    }

    fun GuiGraphicsExtractor.clip(x0: Number, y0: Number, x1: Number, y1: Number, body: () -> Unit) {
        this@clip.enableScissor(x0.ri, y0.ri, x1.ri, y1.ri)
        body()
        this@clip.disableScissor()
    }

    fun GuiGraphicsExtractor.clipOfSize(x: Number, y: Number, width: Number, height: Number, body: () -> Unit) {
        val x1 = x.ri + width.ri
        val y1 = y.ri + height.ri

        this@clipOfSize.clip(x, y, x1, y1, body)
    }

    fun GuiGraphicsExtractor.translate(translate: Vector2f, body: () -> Unit) =
        MatrixTransform(this@translate.pose(), translate)
            .draw(body)
    fun GuiGraphicsExtractor.translate(x: Float, y: Float, body: () -> Unit) =
        this@translate.translate(Vector2f(x, y), body)

    fun GuiGraphicsExtractor.rotate(rotate: Float, pivot: Vector2f?, body: () -> Unit) =
        MatrixTransform(this@rotate.pose(), rotate = rotate, pivot = pivot)
            .draw(body)
    fun GuiGraphicsExtractor.rotate(rotate: Float, ox: Float? = null, oy: Float? = null, body: () -> Unit) {
        val pivot =
            if (ox != null && oy != null) Vector2f(ox, oy)
            else null

        this@rotate.rotate(rotate, pivot, body)
    }

    fun GuiGraphicsExtractor.scale(scale: Vector2f, pivot: Vector2f?, body: () -> Unit) =
        MatrixTransform(this@scale.pose(), scale = scale, pivot = pivot)
            .draw(body)
    fun GuiGraphicsExtractor.scale(width: Float, height: Float, ox: Float?, oy: Float?, body: () -> Unit) {
        val pivot =
            if (ox != null && oy != null) Vector2f(oy, oy)
            else null

        this@scale.scale(Vector2f(width, height), pivot, body)
    }

    fun GuiGraphicsExtractor.transform(translate: Vector2f?, rotate: Float?, scale: Vector2f?, pivot: Vector2f?, body: () -> Unit) =
        MatrixTransform(this@transform.pose(), translate, rotate, scale, pivot)
            .draw(body)
    fun GuiGraphicsExtractor.transform(tx: Float?, ty: Float?, rotate: Float?, sx: Float?, sy: Float?, ox: Float?, oy: Float?, body: () -> Unit) {
        val translate =
            if (tx != null && ty != null) Vector2f(tx, ty)
            else null
        val scale =
            if (sx != null && sy != null) Vector2f(sx, sy)
            else null
        val pivot =
            if (ox != null && oy != null) Vector2f(ox, oy)
            else null

        this@transform.transform(translate, rotate, scale, pivot, body)
    }

    fun GuiGraphicsExtractor.drawNineSliceTexture(
        renderPipeline: RenderPipeline,
        texture: Identifier,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        textureX: Float,
        textureY: Float,
        textureWidth: Float,
        textureHeight: Float,
        margin: Insets,
        tiling: Boolean = false,
        textureTotalWidth: Int = 256,
        textureTotalHeight: Int = 256
    ) = NineSliceTexture(
        this@drawNineSliceTexture,
        renderPipeline, texture, x, y, width, height,
        textureX, textureY, textureWidth, textureHeight,
        margin, tiling, textureTotalWidth, textureTotalHeight
    ).draw()

    fun GuiGraphicsExtractor.drawNineSliceTexture(
        renderPipeline: RenderPipeline,
        texture: Identifier,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        textureX: Float,
        textureY: Float,
        textureWidth: Float,
        textureHeight: Float,
        margin: Float,
        tiling: Boolean = false,
        textureTotalWidth: Int = 256,
        textureTotalHeight: Int = 256
    ) = drawNineSliceTexture(
        renderPipeline,
        texture, x, y, width, height,
        textureX, textureY, textureWidth, textureHeight,
        Insets(margin.toInt(), margin.toInt(), margin.toInt(), margin.toInt()),
        tiling, textureTotalWidth, textureTotalHeight
    )

    fun GuiGraphicsExtractor.drawNineSliceTexture(
        renderPipeline: RenderPipeline,
        texture: Identifier,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        textureX: Float,
        textureY: Float,
        textureWidth: Float,
        textureHeight: Float,
        leftMargin: Int,
        topMargin: Int,
        rightMargin: Int,
        bottomMargin: Int,
        tiling: Boolean = false,
        textureTotalWidth: Int = 256,
        textureTotalHeight: Int = 256
    ) = drawNineSliceTexture(
        renderPipeline,
        texture, x, y, width, height,
        textureX, textureY, textureWidth, textureHeight,
        Insets(topMargin, leftMargin, bottomMargin, rightMargin),
        tiling, textureTotalWidth, textureTotalHeight
    )

    fun GuiGraphicsExtractor.drawNineSliceTexture(
        renderPipeline: RenderPipeline,
        texture: String,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        textureX: Float,
        textureY: Float,
        textureWidth: Float,
        textureHeight: Float,
        margin: Insets,
        tiling: Boolean = false,
        textureTotalWidth: Int = 256,
        textureTotalHeight: Int = 256
    ) = drawNineSliceTexture(
        renderPipeline,
        texture.identifier, x, y, width, height,
        textureX, textureY, textureWidth, textureHeight,
        margin, tiling, textureTotalWidth, textureTotalHeight
    )

    fun GuiGraphicsExtractor.drawNineSliceTexture(
        renderPipeline: RenderPipeline,
        texture: String,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        textureX: Float,
        textureY: Float,
        textureWidth: Float,
        textureHeight: Float,
        margin: Float,
        tiling: Boolean = false,
        textureTotalWidth: Int = 256,
        textureTotalHeight: Int = 256
    ) = drawNineSliceTexture(
        renderPipeline,
        texture.identifier, x, y, width, height,
        textureX, textureY, textureWidth, textureHeight,
        Insets(margin.toInt(), margin.toInt(), margin.toInt(), margin.toInt()),
        tiling, textureTotalWidth, textureTotalHeight
    )

    fun GuiGraphicsExtractor.blitTiled(
        renderPipeline: RenderPipeline,
        texture: Identifier,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        textureX: Float,
        textureY: Float,
        textureWidth: Int,
        textureHeight: Int,
        color: Int = -1
    ) {
        var curY = y
        var remH = height
        while (remH > 0) {
            val chunkH = minOf(remH, textureHeight)
            var curX = x
            var remW = width
            while (remW > 0) {
                val chunkW = minOf(remW, textureWidth)
                this.blit(
                    renderPipeline,
                    texture,
                    curX,
                    curY,
                    textureX,
                    textureY,
                    chunkW,
                    chunkH,
                    textureWidth,
                    textureHeight,
                    color
                )
                curX += chunkW
                remW -= chunkW
            }
            curY += chunkH
            remH -= chunkH
        }
    }
}