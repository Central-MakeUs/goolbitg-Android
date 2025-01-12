package com.project.presentation.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun BaseIcon(
    @DrawableRes iconId: Int,
    modifier: Modifier = Modifier
) {
    val painter = painterResource(iconId)
    Image(modifier = modifier, painter = painter, contentDescription = "base_icon_${iconId}")
}