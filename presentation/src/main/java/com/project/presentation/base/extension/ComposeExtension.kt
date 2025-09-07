package com.project.presentation.base.extension

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

object ComposeExtension {
    fun Modifier.fadingEdge(brush: Brush) = this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.DstIn)
        }

    fun Modifier.innerShadow(
        shape: Shape,
        color: Color,
        blur: Dp,
        offsetY: Dp,
        offsetX: Dp,
        spread: Dp
    ) = drawWithContent {
        drawContent()

        val rect = Rect(Offset.Zero, size)
        val paint = Paint().apply {
            this.color = color
            this.isAntiAlias = true
        }

        val shadowOutline = shape.createOutline(size, layoutDirection, this)

        drawIntoCanvas { canvas ->
            canvas.saveLayer(rect, paint)
            canvas.drawOutline(shadowOutline, paint)

            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            if (blur.toPx() > 0) {
                frameworkPaint.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
            paint.color = Color.Black

            val spreadOffsetX = offsetX.toPx() + if (offsetX.toPx() < 0) -spread.toPx() else spread.toPx()
            val spreadOffsetY = offsetY.toPx() + if (offsetY.toPx() < 0) -spread.toPx() else spread.toPx()

            canvas.translate(spreadOffsetX, spreadOffsetY)
            canvas.drawOutline(shadowOutline, paint)
            canvas.restore()
        }

    }

    @Composable
    fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier{
        return this.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    }

    @Composable
    fun Modifier.rippleClickable(
        onClick: () -> Unit = {}
    ): Modifier {
        return this.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, color = Color(0x0D9E9E9E)),
            onClick = onClick
        )
    }
}
