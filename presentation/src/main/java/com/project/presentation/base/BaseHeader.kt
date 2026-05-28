package com.project.presentation.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.presentation.R
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100

@Composable
fun BaseHeader(
    title: String = "",
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    /**
     * 헤더가 회피해야 할 시스템바 inset.
     * - 화면 root 또는 Scaffold topBar slot 처럼 부모가 inset 처리를 해 주지 않는 위치에 둘 때 default(`WindowInsets.statusBars`) 사용
     * - 부모가 이미 `padding(innerPadding)` 등으로 status bar inset을 적용한 영역 안에 두는 경우 `WindowInsets(0)` 으로 override 해 이중 padding 회피
     */
    windowInsets: WindowInsets = WindowInsets.statusBars,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(
            modifier = Modifier
                .size(64.dp)
                .noRippleClickable{ onBackPressed() }
                .padding(16.dp),
            iconId = R.drawable.ic_arrow_left
        )
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = goolbitgTypography.btn1,
            color = gray100,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(64.dp))
    }
}