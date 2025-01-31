package com.project.presentation.base

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.white

@SuppressLint("DefaultLocale")
@Composable
fun BaseTimePicker(
    modifier: Modifier = Modifier,
    onHourChanged: (String) -> Unit,
    onMinuteChanged: (String) -> Unit,
    onAmpmChanged: (String) -> Unit,
) {
    val hours = remember { (1..12).map { String.format("%02d", it) } }
    val hoursPickerState = rememberPickerState()
    val minutes = remember { (1..59).map { String.format("%02d", it) } }
    val minutesPickerState = rememberPickerState()
    val ampm = remember { listOf("AM", "PM") }
    val ampmPickerState = rememberPickerState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Row(
                modifier = Modifier.width(180.dp).align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BaseInfinityPicker(
                    state = hoursPickerState,
                    items = hours,
                    modifier = Modifier.width(60.dp),
                    visibleItemsCount = 5,
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = onHourChanged
                )
                BaseInfinityPicker(
                    state = minutesPickerState,
                    items = minutes,
                    visibleItemsCount = 5,
                    modifier = Modifier.width(60.dp),
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = onMinuteChanged
                )
                BaseInfinityPicker(
                    state = ampmPickerState,
                    items = ampm,
                    visibleItemsCount = 3,
                    modifier = Modifier.width(60.dp),
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = onAmpmChanged
                )
            }

            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(40.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(roundSm))
                    .background(
                        white.copy(alpha = 0.1f)
                    )
            )
        }
    }
}