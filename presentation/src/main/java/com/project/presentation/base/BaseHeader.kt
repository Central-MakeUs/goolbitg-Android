package com.project.presentation.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.presentation.R
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100

@Composable
fun BaseHeader(
    title: String = "",
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(
            modifier = Modifier
                .size(64.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBackPressed() }
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