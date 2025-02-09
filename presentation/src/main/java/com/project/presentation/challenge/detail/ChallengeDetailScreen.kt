package com.project.presentation.challenge.detail

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.project.domain.model.challenge.ChallengeTripleModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseLoadingBox
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.item.ChallengeDetailCheckEnum
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.roundMd
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlin.math.roundToInt

@Composable
@Preview
fun ChallengeDetailScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    var popupState by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(bg1)) {
        Scaffold(containerColor = transparent) { innerPadding ->
            if (state.value.challengeTripleModel != null) {
                ChallengeDetailContent(
                    modifier = Modifier.padding(innerPadding),
                    nickname = state.value.nickname,
                    challengeTripleModel = state.value.challengeTripleModel!!,
                    onBack = {
                        navHostController.popBackStack()
                    },
                    onStop = {
                        popupState = true
                    },
                    onAttendanceCheck = {
                        viewModel.onEvent(ChallengeDetailEvent.CheckAttendance)
                    }
                )
            }
        }
        if (popupState) {
            ChallengeStopPopup(
                onStopChallenge = {
                    popupState = false
                    viewModel.onEvent(ChallengeDetailEvent.StopChallenge)
                },
                onDismiss = {
                    popupState = false
                }
            )
        }
        if (state.value.isLoading) {
            BaseLoadingBox()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ChallengeDetailContent(
    nickname: String,
    challengeTripleModel: ChallengeTripleModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onStop: () -> Unit,
    onAttendanceCheck: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChallengeDetailHeader(
            title = challengeTripleModel.challenge.title,
            isCanceled = challengeTripleModel.canceled,
            onBack = onBack,
            onStop = onStop
        )

        Spacer(modifier = Modifier.height(36.dp))
        GlideImage(
            modifier = Modifier.size(160.dp),
            model = challengeTripleModel.challenge.imageUrlLarge,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.challenge_detail_sub_title).replace("#VALUE#", nickname),
            style = goolbitgTypography.h4,
            color = white.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.challenge_detail_title).replace(
                "#VALUE#",
                challengeTripleModel.duration.toString()
            ),
            style = goolbitgTypography.h3,
            color = white
        )
        Spacer(modifier = Modifier.height(40.dp))
        // 서버에서 준 Location이 현재 인덱스보다 적고 현재 아이템이 wait이면 none처리를 한다.
        val stateList = listOf(
            challengeTripleModel.check1,
            challengeTripleModel.check2,
            challengeTripleModel.check3
        ).mapIndexed { idx, str ->
            when (str) {
                "SUCCESS" -> ChallengeDetailCheckEnum.Check
                "WAIT" -> {
                    if (idx > challengeTripleModel.location - 1) {
                        ChallengeDetailCheckEnum.Disable
                    } else {
                        ChallengeDetailCheckEnum.Enable
                    }
                }

                else -> ChallengeDetailCheckEnum.Disable
            }
        }
        ChallengeDetailCheckContent(
            checkStateList = stateList,
            onAttendanceCheck = onAttendanceCheck
        )
        Spacer(modifier = Modifier.height(40.dp))

        ChallengeDetailInfo(
            progressCount = challengeTripleModel.challenge.participantCount,
            avgPercent = challengeTripleModel.challenge.avgAchieveRatio.roundToInt(),
            maxDay = challengeTripleModel.challenge.maxAchieveDays
        )

    }
}

@Composable
fun ChallengeDetailHeader(
    title: String,
    isCanceled: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(86.dp)
                .fillMaxHeight()
        ) {
            BaseIcon(
                modifier = Modifier
                    .size(64.dp)
                    .noRippleClickable {
                        onBack()
                    }
                    .padding(16.dp),
                iconId = R.drawable.ic_arrow_left
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = gray100,
            style = goolbitgTypography.h3,
            textAlign = TextAlign.Center
        )

        if(isCanceled){
            Box(modifier = Modifier.width(86.dp))
        }else{
            Box(modifier = Modifier.width(86.dp)
                .fillMaxHeight()
                .noRippleClickable {
                    onStop()
                }
            ){
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.common_stop),
                    color = com.project.presentation.ui.theme.error,
                    style = goolbitgTypography.h3,
                )
            }
        }
    }
}

@Composable
fun ChallengeDetailCheckContent(
    checkStateList: List<ChallengeDetailCheckEnum>,
    modifier: Modifier = Modifier,
    onAttendanceCheck: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        checkStateList.forEachIndexed { idx, state ->
            CheckContentItem(
                checkState = state,
                text = stringResource(R.string.challenge_detail_day).replace(
                    "#VALUE#",
                    (idx + 1).toString()
                ),
                onItemClick = onAttendanceCheck
            )
        }
    }
}

@Composable
fun CheckContentItem(
    checkState: ChallengeDetailCheckEnum,
    text: String,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .then(
                if (checkState == ChallengeDetailCheckEnum.Enable) {
                    Modifier.noRippleClickable {
                        onItemClick()
                    }
                } else {
                    Modifier
                }
            )
    ) {
        BaseIcon(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopCenter),
            iconId = checkState.drawableId
        )
        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = text,
            color = checkState.textColor,
            style = if (checkState == ChallengeDetailCheckEnum.Check)
                goolbitgTypography.h4.copy(fontWeight = FontWeight.SemiBold)
            else
                goolbitgTypography.h4
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
            text = stringResource(R.string.challenge_detail_info_1).replace(
                "#VALUE#",
                progressCount.toString()
            )
        )
        ChallengeDetailInfoItem(
            iconId = R.drawable.ic_challenge_detail_info_2,
            text = stringResource(R.string.challenge_detail_info_2).replace(
                "#VALUE#",
                avgPercent.toString()
            )
        )
        ChallengeDetailInfoItem(
            iconId = R.drawable.ic_challenge_detail_info_3,
            text = stringResource(R.string.challenge_detail_info_3).replace(
                "#VALUE#",
                maxDay.toString()
            )
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
            .noRippleClickable {  }
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
                        .noRippleClickable { onDismiss() }
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
                        .noRippleClickable { onStopChallenge() }
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
