package com.project.presentation.home

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.domain.model.challenge.ChallengeRecordModel
import com.project.domain.model.user.WeeklyRecordStatusModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTintIcon
import com.project.presentation.base.extension.ComposeExtension.innerShadow
import com.project.presentation.base.extension.StringExtension.priceComma
import com.project.presentation.common.DayOfWeekEnum
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.main15
import com.project.presentation.ui.theme.main50
import com.project.presentation.ui.theme.roundMd
import com.project.presentation.ui.theme.roundXs
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import java.text.DecimalFormat

@Composable
@Preview
fun HomeScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent,
            bottomBar = {
                BaseBottomNavBar(navController = navHostController)
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                HomeHeader(modifier = Modifier.fillMaxWidth(),
                    newAlarmCount = 1,
                    onNotification = {}
                )

                HomeContent(
                    dayList = state.value.dayList,
                    weeklyRecordStatusModel = state.value.weeklyRecordStatusModel,
                    todayChallengeList = state.value.challengeRecordModel,
                    onChallengeSelected = {

                    }
                )
            }
        }
    }
}

@Composable
fun HomeHeader(
    newAlarmCount: Int = 0,
    modifier: Modifier = Modifier,
    onNotification: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Row(modifier = Modifier.weight(1f)) {
            BaseIcon(iconId = R.drawable.ic_home_logo_icon)
            Spacer(modifier = Modifier.width(8.dp))
            BaseIcon(iconId = R.drawable.ic_home_logo_text)
        }
        Box(modifier = Modifier
            .size(31.dp, 30.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onNotification() }) {
            BaseIcon(
                modifier = Modifier.align(Alignment.BottomStart),
                iconId = R.drawable.ic_notification
            )
            if (newAlarmCount != 0) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(com.project.presentation.ui.theme.error)
                        .align(Alignment.TopEnd)
                ) {

                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = newAlarmCount.toString(),
                        style = goolbitgTypography.body5,
                        color = white,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    dayList: List<Int>,
    weeklyRecordStatusModel: WeeklyRecordStatusModel?,
    todayChallengeList: List<ChallengeRecordModel>,
    modifier: Modifier = Modifier,
    onChallengeSelected: (ChallengeRecordModel) -> Unit
) {
    val saving = weeklyRecordStatusModel?.saving ?: 0
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_title).replace("#VALUE#", weeklyRecordStatusModel?.nickname ?: ""),
                style = goolbitgTypography.h3, color = white
            )
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                RouletteNumberAnimation(
                    targetNumber = saving,
                    digitCount = saving.toString().length
                )
                Text(
                    text = stringResource(R.string.home_sub_title),
                    style = goolbitgTypography.h4,
                    color = white
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HomeChallengeDetailInfo(price = saving, continuousDay = weeklyRecordStatusModel?.continueCount ?: 0)
        }
        item {
            HomeWeekCard(
                dayList = dayList,
                weeklyRecordStatusModel = weeklyRecordStatusModel
            )
        }
        item {
            TodayTodoListContent(
                todayChallengeList = todayChallengeList,
                onChallengeSelected = onChallengeSelected
            )
        }
    }
}

@Composable
fun RouletteNumberAnimation(
    targetNumber: Int,
    digitCount: Int,
    modifier: Modifier = Modifier
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(targetNumber) {
        animatable.animateTo(
            targetValue = (targetNumber).toFloat(), // 목표보다 더 크게 롤링 후 멈춤
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    fun formatAmount(amount: Int, digitCount: Int): String {
        var pattern = ""
        var dc = digitCount
        while (dc > 0) {
            if (dc > 3) {
                pattern = "$pattern,000"
            } else {
                pattern = "0".repeat(dc) + pattern
                dc = 0
            }
            dc -= 3
        }

        val decimalFormat = DecimalFormat(pattern)
        return decimalFormat.format(amount)
    }

    Text(
        modifier = modifier.width(30.dp * digitCount),
        text = formatAmount(animatable.value.toInt(), digitCount),
        style = goolbitgTypography.display,
        color = white
    )
}

@Composable
fun HomeChallengeDetailInfo(
    modifier: Modifier = Modifier,
    continuousDay: Int,
    price: Int,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(roundXs))
            .border(
                width = 1.dp,
                color = white.copy(alpha = 0.3f),
                shape = RoundedCornerShape(roundXs)
            )
            .background(white.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseTintIcon(iconId = R.drawable.ic_challenge_reword_12, tint = white.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.home_achieved_day).replace(
                "#VALUE#",
                continuousDay.toString()
            ),
            style = goolbitgTypography.body5,
            color = white.copy(alpha = 0.5f)
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .width(1.dp)
                .height(16.dp)
                .background(white.copy(alpha = 0.4f))
        )
        BaseTintIcon(iconId = R.drawable.ic_challenge_price_12, tint = white.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = getChallengePricePhrase(context = context, price = price),
            style = goolbitgTypography.body5,
            color = white.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun HomeWeekCard(
    modifier: Modifier = Modifier,
    dayList: List<Int>,
    weeklyRecordStatusModel: WeeklyRecordStatusModel?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .height(123.dp)
            .clip(RoundedCornerShape(roundMd))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        main100, Color(0xFF67BF4E)
                    )
                )
            )
            .innerShadow(
                shape = RoundedCornerShape(roundMd),
                color = white.copy(alpha = 0.5f),
                blur = 4.dp,
                offsetY = 4.dp,
                offsetX = 4.dp,
                spread = 0.dp,
            )
            .innerShadow(
                shape = RoundedCornerShape(roundMd),
                color = black.copy(alpha = 0.3f),
                blur = 4.dp,
                offsetY = (-4).dp,
                offsetX = (-4).dp,
                spread = 0.dp,
            )
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            val dayOfWeekList = DayOfWeekEnum.entries
            weeklyRecordStatusModel?.let {
                it.weeklyStatus.forEachIndexed { idx, item ->
                    Column(
                        modifier = Modifier.width(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (it.todayIndex == idx) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(white)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(dayOfWeekList[idx].strId2),
                            color = white,
                            style = goolbitgTypography.caption1
                        )
                    }
                    if (idx != dayOfWeekList.size - 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            weeklyRecordStatusModel?.let {
                it.weeklyStatus.forEachIndexed { idx, item ->
                    val isStamp =
                        item.totalChallenges != 0 && item.totalChallenges == item.achievedChallenges
                    if (isStamp) {
                        BaseIcon(
                            modifier = Modifier.size(40.dp),
                            iconId = R.drawable.img_home_stamp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = white.copy(alpha = 0.15f),
                                    shape = CircleShape
                                )
                                .background(transparent)
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = dayList[idx].toString(),
                                style = goolbitgTypography.body1,
                                color = white.copy(alpha = 0.3f),
                            )
                        }
                    }
                    if (idx != it.weeklyStatus.size - 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun TodayTodoListContent(
    modifier: Modifier = Modifier,
    todayChallengeList: List<ChallengeRecordModel>,
    onChallengeSelected: (ChallengeRecordModel) -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.home_today_list_title),
            color = white,
            style = goolbitgTypography.h4
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.home_today_list_subtitle).replace(
                "#VALUE#",
                todayChallengeList.sumOf { it.challenge.reward }.priceComma()
            ),
            color = gray300,
            style = goolbitgTypography.body4,
            textAlign = TextAlign.End
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    todayChallengeList.forEach {
        TodayTodoItem(
            todayChallenge = it,
            onItemClick = { onChallengeSelected(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
fun TodayTodoItem(
    todayChallenge: ChallengeRecordModel,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    val isSuccess = todayChallenge.status == "SUCCESS"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .border(width = 1.dp, color = if (isSuccess) main15 else gray500, shape = CircleShape)
            .background(gray700)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onItemClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val checkDrawableId =
            if (isSuccess) R.drawable.ic_checkbox_green else R.drawable.ic_checkbox_gray
        BaseIcon(modifier = Modifier.size(24.dp), iconId = checkDrawableId)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = todayChallenge.challenge.title,
            color = if (isSuccess) main100 else white,
            style = goolbitgTypography.body4
        )
        Text(
            text = "+${todayChallenge.challenge.reward.priceComma()}",
            color = if (isSuccess) main50 else gray400,
            style = goolbitgTypography.body5
        )
    }
}

fun getChallengePricePhrase(context: Context, price: Int): String {
    return when (price) {
        in (2000 until 5000) -> context.getString(R.string.home_price_phrase_0)
        in (5000 until 10000) -> context.getString(R.string.home_price_phrase_1)
        in (10000 until 20000) -> context.getString(R.string.home_price_phrase_2)
        in (20000 until 30000) -> context.getString(R.string.home_price_phrase_3)
        in (30000 until 40000) -> context.getString(R.string.home_price_phrase_4)
        in (40000 until 50000) -> context.getString(R.string.home_price_phrase_5)
        in (50090 until 100000) -> context.getString(R.string.home_price_phrase_6)
        else -> context.getString(R.string.home_price_phrase_7)
    }
}
