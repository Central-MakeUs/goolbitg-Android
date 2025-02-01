package com.project.presentation.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.project.presentation.ui.theme.white

@Composable
fun BaseIcon(
    @DrawableRes iconId: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val painter = painterResource(iconId)
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = "base_icon_${iconId}",
        contentScale = contentScale
    )
}

@Composable
fun BaseTintIcon(
    @DrawableRes iconId: Int,
    modifier: Modifier = Modifier,
    tint: Color = white
) {
    val painter = painterResource(iconId)
    Icon(
        modifier = modifier,
        painter = painter,
        tint = tint,
        contentDescription = "base_icon_${iconId}",
    )
}
