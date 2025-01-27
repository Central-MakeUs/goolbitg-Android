package com.project.presentation.challenge

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.item.ChallengeDetailCheckEnum
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.roundMd
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@Composable
@Preview
fun ChallengeDetailScreen(navHostController: NavHostController = rememberNavController()) {
    var popupState by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(containerColor = transparent) { innerPadding ->
            ChallengeDetailContent(
                modifier = Modifier.padding(innerPadding),
                headerTitle = "커피 값 모아 태산",
                nickname = "어린 굴비",
                day = "1",
                progressCount = 10,
                avgPercent = 66,
                maxDay = 30,
                onBack = { },
                onStop = {
                    popupState = true
                }
            )
        }
        if (popupState) {
            ChallengeStopPopup(
                onStopChallenge = {},
                onDismiss = {
                    popupState = false
                }
            )
        }
    }
}

@Composable
fun ChallengeDetailContent(
    headerTitle: String,
    nickname: String,
    day: String,
    progressCount: Int,
    avgPercent: Int,
    maxDay: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChallengeDetailHeader(
            title = headerTitle,
            onBack = onBack,
            onStop = onStop
        )

        Spacer(modifier = Modifier.height(36.dp))
        BaseIcon(modifier = Modifier.size(160.dp), iconId = R.drawable.illu_coffee)
        Text(
            text = stringResource(R.string.challenge_detail_sub_title).replace("#VALUE#", nickname),
            style = goolbitgTypography.h4,
            color = white.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.challenge_detail_title).replace("#VALUE#", day),
            style = goolbitgTypography.h3,
            color = white
        )
        Spacer(modifier = Modifier.height(40.dp))
        ChallengeDetailCheckContent(
            checkStateList = listOf(
                ChallengeDetailCheckEnum.Check,
                ChallengeDetailCheckEnum.Enable,
                ChallengeDetailCheckEnum.Disable
            )
        )
        Spacer(modifier = Modifier.height(40.dp))
        ChallengeDetailInfo(
            progressCount = progressCount,
            avgPercent = avgPercent,
            maxDay = maxDay
        )

    }
}


@Composable
fun ChallengeDetailHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: icon 바꾸기
        BaseIcon(modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onBack()
            }
            .padding(16.dp), iconId = R.drawable.ic_checkbox_gray)

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = gray100,
            style = goolbitgTypography.h3,
            textAlign = TextAlign.Center
        )

        Text(modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onStop()
            }
            .padding(16.dp),
            text = stringResource(R.string.common_stop),
            color = com.project.presentation.ui.theme.error,
            style = goolbitgTypography.h3)
    }
}

@Composable
fun ChallengeDetailCheckContent(
    checkStateList: List<ChallengeDetailCheckEnum>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        checkStateList.forEachIndexed { idx, checkState ->
            CheckContentItem(
                checkState = checkState,
                text = stringResource(R.string.challenge_detail_day).replace("#VALUE#", (idx + 1).toString())
            )
        }
    }
}

@Composable
fun CheckContentItem(
    checkState: ChallengeDetailCheckEnum,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BaseIcon(modifier = Modifier.size(56.dp), iconId = checkState.drawableId)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            color = checkState.textColor,
            style = if (checkState == ChallengeDetailCheckEnum.Check) goolbitgTypography.h3 else goolbitgTypography.h4
        )
    }
}

@Composable
fun ChallengeDetailInfo(
    progressCount: Int,
    avgPercent: Int,
    maxDay: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(roundSm))
            .border(width = 1.dp, shape = RoundedCornerShape(roundSm), color = gray400)
            .background(gray500)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ChallengeDetailInfoItem(
            iconId = R.drawable.ic_challenge_detail_info_1,
            text = stringResource(R.string.challenge_detail_info_1).replace("#VALUE#", progressCount.toString())
        )
        ChallengeDetailInfoItem(
            iconId = R.drawable.ic_challenge_detail_info_2,
            text = stringResource(R.string.challenge_detail_info_2).replace("#VALUE#", avgPercent.toString())
        )
        ChallengeDetailInfoItem(
            iconId = R.drawable.ic_challenge_detail_info_3,
            text = stringResource(R.string.challenge_detail_info_3).replace("#VALUE#", maxDay.toString())
        )
    }
}

@Composable
fun ChallengeDetailInfoItem(
    @DrawableRes iconId: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(iconId = iconId)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = white, style = goolbitgTypography.body4)
    }
}


@Composable
fun ChallengeStopPopup(
    modifier: Modifier = Modifier,
    onStopChallenge: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(black.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(roundMd))
                .border(
                    width = 1.dp, color = gray400, shape = RoundedCornerShape(roundMd)
                )
                .background(gray500)
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.challenge_detail_stop_title),
                color = white,
                style = goolbitgTypography.h3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.challenge_detail_stop_sub_title),
                color = gray100,
                style = goolbitgTypography.caption2,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(gray400)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onDismiss() }
                        .padding(vertical = 10.dp),
                    text = stringResource(R.string.common_cancel),
                    color = white,
                    style = goolbitgTypography.btn2,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(com.project.presentation.ui.theme.error)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onStopChallenge() }
                        .padding(vertical = 10.dp),
                    text = stringResource(R.string.common_stop),
                    color = white,
                    style = goolbitgTypography.btn2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
