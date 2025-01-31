package com.project.presentation.base

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.main100

@Composable
fun BaseBar(
    progress: Float,
    bgColor: Color = gray500,
    progressColor: Color = main100,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.height / 2f
        drawRoundRect(
            color = bgColor,
            topLeft = Offset(x = 0f, y = 0f),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = progressColor,
            topLeft = Offset(x = 0f, y = 0f),
            size = Size(size.width * progress / 100f, size.height),
            cornerRadius = CornerRadius(radius, radius)
        )
    }
}