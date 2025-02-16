package com.project.presentation.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.presentation.R
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.roundMd
import com.project.presentation.ui.theme.white

@Composable
fun BaseTwoButtonPopup(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String = "",
    confirmText: String = stringResource(R.string.common_confirm),
    dismissText: String = stringResource(R.string.common_cancel),
    topIconId: Int? = null,
    confirmTextColor: Color = white,
    dismissTextColor: Color = gray100,
    confirmBgColor: Color = com.project.presentation.ui.theme.error,
    dismissBgColor: Color = gray400,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(black.copy(alpha = 0.5f))
            .noRippleClickable { },
        contentAlignment = Alignment.Center
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.7f)
                .clip(RoundedCornerShape(roundMd))
                .border(width = 1.dp, color = gray400, shape = RoundedCornerShape(roundMd))
                .background(gray500)
                .padding(16.dp)
        ) {
            if (topIconId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    BaseIcon(iconId = topIconId)
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                color = white,
                style = goolbitgTypography.h3,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = subTitle,
                color = Color(0xFF9CA3AF),
                style = goolbitgTypography.caption2,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(dismissBgColor)
                        .noRippleClickable { onDismiss() }
                        .padding(vertical = 10.5.dp),
                    text = dismissText, color = dismissTextColor, style = goolbitgTypography.btn2,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(confirmBgColor)
                        .noRippleClickable { onConfirm() }
                        .padding(vertical = 10.5.dp),
                    text = confirmText, color = confirmTextColor, style = goolbitgTypography.btn2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
