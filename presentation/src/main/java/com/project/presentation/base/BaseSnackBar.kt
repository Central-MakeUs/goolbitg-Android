package com.project.presentation.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.white

@Composable
fun BaseSnackBar(
    snackBarData: SnackbarData,
    modifier: Modifier = Modifier,
    bgColor: Color = white.copy(alpha = 0.2f)
) {
    Box(modifier = modifier.padding(bottom = 24.dp)) {
        Row(
            modifier =
            Modifier
                .clip(CircleShape)
                .background(bgColor)
                .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp)
                .align(Alignment.Center),
        ) {
            Text(
                text = snackBarData.visuals.message,
                style = goolbitgTypography.body3,
                color = white,
            )
        }
    }
}
