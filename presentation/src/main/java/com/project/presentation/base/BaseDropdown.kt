package com.project.presentation.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.white

/**
 * 드롭다운 Base Input
 * @author 이유호
 * @param title 타이틀, default: emptyString
 * @param hint placeholder text, default: emptyString
 * @param titleColor 타이틀 color, default: white
 * @param valueColor value color, default: white
 * @param hintColor hint color, default: gray300
 * @param bgColor background color, default: gray300
 * @param selectedItem 현재 선택된 아이템, default: null
 * @param itemList 드롭다운 목록에 보여줄 리스트, default: emptyList
 * @param modifier Modifier
 * @param onItemSelected 드롭다운 아이템을 선택했을 때 수행할 작업, Required
 */
@Composable
fun BaseFormDropdown(
    title: String = "",
    hint: String = "",
    titleColor: Color = white,
    valueColor: Color = white,
    hintColor: Color = gray300,
    bgColor: Color = gray600,
    selectedItem: String? = null,
    itemList: List<String> = listOf(),
    modifier: Modifier = Modifier,
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(text = title, style = goolbitgTypography.caption1, color = titleColor)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(
                    width = 0.5.dp,
                    shape = RoundedCornerShape(4.dp),
                    color = gray500
                )
                .background(bgColor)
        ) {
            Row(
                modifier = Modifier
                    .noRippleClickable {
                        if (itemList.isNotEmpty()) {
                            expanded = !expanded
                        }
                    }
                    .padding(start = 24.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedItem == null) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = hint,
                        style = goolbitgTypography.caption2,
                        color = hintColor
                    )
                } else {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = selectedItem,
                        style = goolbitgTypography.caption2,
                        color = valueColor
                    )
                }

                val icon =
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = icon,
                    tint = gray300,
                    contentDescription = "BaseFormDropdownArrow"
                )
            }

            if (expanded) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(items = itemList) { index, item ->
                        DropdownItem(
                            text = item,
                            textColor = white,
                            onClick = {
                                onItemSelected(index)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DropdownItem(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(0.5.dp)
        .background(gray500))
    Text(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp), text = text, color = textColor,
        style = goolbitgTypography.body3
    )
}