package com.project.presentation.base

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray800
import com.project.presentation.ui.theme.white

@Composable
fun BaseTab(
    items: List<String>,
    modifier: Modifier,
    indicatorPadding: Dp = 4.dp,
    spacePadding: Dp = 15.dp,
    selectedItemIndex: Int = 0,
    onSelectedTab: (index: Int) -> Unit
) {
    var tabWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val tabItemWidth by remember {
        derivedStateOf { mutableStateOf((tabWidth - indicatorPadding * 2 - spacePadding) / 2) }
    }

    val indicatorOffset: Dp by animateDpAsState(
        if (selectedItemIndex == 0) {
            indicatorPadding
        } else {
            indicatorPadding + tabItemWidth.value + spacePadding
        }
    )

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                tabWidth = with(density) { coordinates.size.width.toDp() }
            }
            .background(color = gray500, shape = CircleShape)
    ) {
        MyTabIndicator(
            modifier = Modifier
                .padding(vertical = indicatorPadding)
                .fillMaxHeight()
                .width(tabItemWidth.value),
            indicatorOffset = indicatorOffset
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(indicatorPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { index, title ->
                MyTabItem(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = {
                        onSelectedTab(index)
                    },
                    title = title,
                    isSelected = index == selectedItemIndex
                )
                if (index != items.size - 1) {
                    Spacer(modifier = Modifier.width(spacePadding))
                }
            }
        }

    }
}

@Composable
private fun MyTabIndicator(
    modifier: Modifier,
    indicatorOffset: Dp,
) {
    Box(
        modifier = modifier
            .offset(x = indicatorOffset)
            .clip(CircleShape)
            .background(white)
    )
}


@Composable
private fun MyTabItem(
    modifier: Modifier,
    onClick: () -> Unit,
    title: String,
    isSelected: Boolean
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .noRippleClickable { onClick() },
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title,
            style = if (isSelected) goolbitgTypography.btn4 else goolbitgTypography.body4,
            color = if(isSelected) gray800 else gray300
        )
    }
}