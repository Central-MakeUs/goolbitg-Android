package com.project.presentation.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.white

@Composable
fun BaseBottomBtn(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = white,
    backgroundColor: Color? = null,
    visible: Boolean = true,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .then(
                    if (backgroundColor != null) {
                        Modifier.background(backgroundColor)
                    } else {
                        Modifier.background(
                            brush = Brush.horizontalGradient(listOf(main100, Color(0xFF67BF4E)))
                        )
                    }
                )
                .noRippleClickable {
                    onClick()
                }
                .padding(vertical = 16.dp),
            text = text,
            textAlign = TextAlign.Center,
            style = goolbitgTypography.btn1,
            color = textColor
        )
    }
}

@Composable
fun BaseBottomBtn(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .then(
                if (enabled) {
                    Modifier.background(
                        brush = Brush.horizontalGradient(listOf(main100, Color(0xFF67BF4E)))
                    )
                } else {
                    Modifier.background(gray500)
                }
            )
            .noRippleClickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        text = text,
        textAlign = TextAlign.Center,
        style = goolbitgTypography.btn1,
        color = if (enabled) white else gray400
    )
}

@Composable
fun BaseKeyboardBottomBtn(
    modifier: Modifier = Modifier,
    text: String,
    isKeyboard: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier
            .then(
                if (!isKeyboard) {
                    Modifier
                        .padding(16.dp)
                        .clip(CircleShape)
                } else {
                    Modifier
                }
            )
            .then(
                if (enabled) {
                    Modifier
                        .background(
                            brush = Brush.horizontalGradient(listOf(main100, Color(0xFF67BF4E))),
                        )
                        .noRippleClickable {
                            onClick()
                        }
                } else {
                    Modifier.background(gray500)
                }
            )
            .padding(vertical = 16.dp),
        text = text,
        textAlign = TextAlign.Center,
        style = goolbitgTypography.btn1,
        color = if (enabled) white else gray400
    )
}
