package com.project.presentation.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.project.presentation.R
import com.project.presentation.base.BaseTab
import com.project.presentation.base.BaseBar
import com.project.presentation.base.BaseIcon
import com.project.presentation.common.DayOfWeekEnum
import com.project.presentation.item.ChallengeInfo
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import java.time.LocalDate

@Composable
@Preview(widthDp = 400)
fun ChallengeScreen(
    navHostController: NavHostController = rememberNavController()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(containerColor = transparent,
            bottomBar = {
                BaseBottomNavBar(navController = navHostController)
            }
        ) { innerPadding ->
            ChallengeContent(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun ChallengeContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        ChallengeHeader(
            modifier = Modifier.fillMaxWidth(),
            isGroup = false,
            onIndividual = {},
            onGroup = {},
            onAddClick = {}
        )
        ChallengeDateSelector(text = "2025년 01월 20일")
        ChallengeWeeklyCalendar(modifier = Modifier.fillMaxWidth())
        var selectedTabIdx by remember { mutableStateOf(0) }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            BaseTab(
                modifier = Modifier.padding(vertical = 8.dp).size(247.dp, 41.dp),
                items = listOf("진행 중","진행 완료"),
                selectedItemIndex = selectedTabIdx,
                onSelectedTab = {selectedTabIdx = it}
            )
        }
        val list = List(10) { ChallengeInfo(id = 1, title = "ff", subTitle = "fsg", imgUrl = "", savedPrice = 7000) }
        ChallengeListContent(modifier = Modifier.weight(1f), challengeList = list)
    }
}

@Composable
fun ChallengeHeader(
    isGroup: Boolean,
    modifier: Modifier = Modifier,
    onIndividual: () -> Unit,
    onGroup: () -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onIndividual() },
                text = stringResource(R.string.challenge_individual),
                style = if (isGroup) goolbitgTypography.h2 else goolbitgTypography.h1,
                color = if (isGroup) gray400 else white
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onGroup() },
                text = stringResource(R.string.challenge_group),
                style = if (isGroup) goolbitgTypography.h1 else goolbitgTypography.h2,
                color = if (isGroup) white else gray400
            )
        }
        BaseIcon(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onAddClick()
                }
                .padding(8.dp),
            iconId = R.drawable.ic_plus
        )

    }
}

@Composable
fun ChallengeDateSelector(
    modifier: Modifier = Modifier,
    text: String,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = goolbitgTypography.body1, color = gray50)
        Spacer(modifier = Modifier.width(8.dp))
        BaseIcon(iconId = R.drawable.ic_arrow_down)
    }
}


@Composable
fun ChallengeWeeklyCalendar(modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    val infiniteCenter = Int.MAX_VALUE / 2 // 무한 스크롤의 중심값
    val pagerState = rememberPagerState(initialPage = infiniteCenter,
        pageCount = { Int.MAX_VALUE })

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
    ) { page ->
        // 현재 페이지에서의 시작 날짜 계산
        val currentWeekStart = today.plusDays((page - infiniteCenter) * 7L)
        val weekDates = getWeekDates(currentWeekStart)

        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            weekDates.forEachIndexed { idx, date ->
                if (idx != 0) {
                    Spacer(modifier = Modifier.weight(12f))
                }
                CalendarItem(
                    modifier = Modifier.weight(40f),
                    dayOfWeek = date.dayOfWeek.value,
                    day = date.dayOfMonth.toString()
                )
            }
        }
    }
}

@Composable
fun CalendarItem(
    day: String,
    dayOfWeek: Int,
    modifier: Modifier = Modifier
) {
    val ratio = 40f / 111f
    val dayOfWeekStr = stringResource(DayOfWeekEnum.entries[dayOfWeek - 1].strId2)
    Column(modifier = modifier.aspectRatio(ratio)) {
        Spacer(modifier = Modifier.weight(20f))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(19f),
            text = dayOfWeekStr,
            textAlign = TextAlign.Center,
            style = goolbitgTypography.caption1,
            color = gray200
        )
        Spacer(modifier = Modifier.weight(4f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(60f)
        ) {
            Spacer(modifier = Modifier.weight(8f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(36f)
                    .aspectRatio(1f)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    text = day,
                    style = goolbitgTypography.body1,
                    color = white
                )
            }
            Box(modifier = Modifier.weight(16f)) {
                BaseBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.Center),
                    progress = 100f
                )
            }
        }
        Spacer(modifier = Modifier.weight(8f))
    }
}

@Composable
fun ChallengeListContent(
    challengeList: List<ChallengeInfo>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = challengeList) {
            ChallengeListItem(item = it)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ChallengeListItem(
    modifier: Modifier = Modifier,
    item: ChallengeInfo
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                modifier = Modifier.size(45.dp),
                model = item.imgUrl,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = item.title, style = goolbitgTypography.body3, color = white)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.subTitle, style = goolbitgTypography.body5, color = white)
            }
            BaseIcon(iconId = R.drawable.ic_arrow_right_over)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 24.dp)
                .background(gray500)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ChallengeProgressSwitch(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .width(247.dp)
                .clip(CircleShape)
                .background(gray500)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Spacer(modifier = Modifier.width(15.dp))
        }
    }
}

fun getWeekDates(startDate: LocalDate): List<LocalDate> {
    val startOfWeek = startDate.with(java.time.DayOfWeek.MONDAY) // 시작 날짜를 월요일로 고정
    return (0 until 7).map { startOfWeek.plusDays(it.toLong()) }
}
