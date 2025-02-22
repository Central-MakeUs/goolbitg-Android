package com.project.presentation.home

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.domain.model.challenge.ChallengeRecordModel
import com.project.domain.model.user.WeeklyRecordStatusModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseLoadingBox
import com.project.presentation.base.BaseSnackBar
import com.project.presentation.base.BaseTintIcon
import com.project.presentation.base.extension.ComposeExtension.fadingEdge
import com.project.presentation.base.extension.ComposeExtension.innerShadow
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.base.extension.StringExtension.priceComma
import com.project.presentation.common.DayOfWeekEnum
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.main15
import com.project.presentation.ui.theme.main20
import com.project.presentation.ui.theme.main50
import com.project.presentation.ui.theme.roundMd
import com.project.presentation.ui.theme.roundXs
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
@Preview
fun HomeScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // 현재 화면에 해당하는 backStackEntry를 가져옴
    val currentBackStackEntry = remember { navHostController.getBackStackEntry(NavItem.Home.route) }
    // Lifecycle을 감지하여 ON_RESUME 이벤트에 반응
    DisposableEffect(currentBackStackEntry) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch {
                    viewModel.getWeekDays()
                    viewModel.getWeeklyRecordStatus()
                    viewModel.fetchEnrolledChallengeList()
                }
            }
        }
        currentBackStackEntry.lifecycle.addObserver(observer)
        onDispose {
            currentBackStackEntry.lifecycle.removeObserver(observer)
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var backPressedState by remember { mutableStateOf(true) }
    var backPressedTime = 0L

    BackHandler {
        if(System.currentTimeMillis() - backPressedTime <= 1000L) {
            // 앱 종료
            (context as Activity).finish()
        } else {
            backPressedState = true
            coroutineScope.launch {
                val job =
                    launch {
                        snackBarHostState.showSnackbar(
                            message = "종료하시려면 한 번 더 눌러주세요.",
                            withDismissAction = true,
                        )
                    }
                delay(3000L)
                job.cancel()
            }
        }
        backPressedTime = System.currentTimeMillis()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent,
            bottomBar = {
                BaseBottomNavBar(navController = navHostController)
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    snackbar = {
                        BaseSnackBar(
                            modifier = Modifier.fillMaxWidth(),
                            bgColor = gray400,
                            snackBarData = it,
                        )
                    },
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .consumeWindowInsets(WindowInsets.navigationBars)
                        .imePadding(),
                )
            }
        ) { innerPadding ->
            HomeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(transparent)
                    .padding(innerPadding),
                dayList = state.value.dayList,
                weeklyRecordStatusModel = state.value.weeklyRecordStatusModel,
                todayChallengeList = state.value.challengeRecordModel,
                onChallengeSelected = {
                    val route = NavItem.ChallengeDetail.route.replace(
                        "{challengeId}",
                        "${it.challenge.id}"
                    )
                    navHostController.navigate(route)
                }
            )
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
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Row(modifier = Modifier.weight(1f)) {
            BaseIcon(iconId = R.drawable.ic_home_logo_icon)
            Spacer(modifier = Modifier.width(8.dp))
            BaseIcon(iconId = R.drawable.ic_home_logo_text)
        }
        Box(modifier = Modifier
            .size(31.dp, 30.dp)
            .noRippleClickable { onNotification() }) {
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
    val listState = rememberLazyListState()
    // 첫 번째 아이템과 마지막 아이템 가시성을 추적하는 상태
    val showTopFade by remember {
        derivedStateOf { listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0 }
    }
    val showBottomFade by remember {
        derivedStateOf {
            (listState.layoutInfo.totalItemsCount > 0 &&
                    listState.layoutInfo.visibleItemsInfo.lastOrNull()?.let { lastItem ->
                        lastItem.index == listState.layoutInfo.totalItemsCount - 1 &&
                                lastItem.offset + lastItem.size <= listState.layoutInfo.viewportEndOffset
                    } ?: true).not()
        }
    }

    val listFade by remember {
        derivedStateOf {
            when {
                showTopFade && showBottomFade -> Brush.verticalGradient(
                    0f to transparent,
                    0.03f to white,
                    0.97f to white,
                    1f to transparent
                )

                showTopFade -> Brush.verticalGradient(0f to transparent, 0.03f to white)
                showBottomFade -> Brush.verticalGradient(0.97f to white, 1f to transparent)
                else -> null
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize()
            .then(
                if (listFade != null) {
                    Modifier.fadingEdge(listFade!!)
                } else {
                    Modifier
                }
            ),
        state = listState
    ) {
        item {
            val saving = weeklyRecordStatusModel?.saving ?: 0
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier
                        .size(221.34.dp, 280.dp)
                        .padding(top = 34.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 21.26.dp),
                    painter = painterResource(R.drawable.img_home_bg),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    HomeHeader(modifier = Modifier.fillMaxWidth(),
                        newAlarmCount = 1,
                        onNotification = {}
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.home_title).replace(
                            "#VALUE#",
                            weeklyRecordStatusModel?.nickname ?: ""
                        ),
                        style = goolbitgTypography.h3, color = white
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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

                    Spacer(modifier = Modifier.height(8.dp))
                    HomeChallengeDetailInfo(
                        price = saving,
                        continuousDay = weeklyRecordStatusModel?.continueCount ?: 0
                    )

                    HomeWeekCard(
                        dayList = dayList,
                        weeklyRecordStatusModel = weeklyRecordStatusModel
                    )
                }
            }
        }
        item {
            TodayTodoListContent(
                modifier = Modifier.padding(horizontal = 16.dp),
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
        modifier = modifier.width(29.dp * digitCount),
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
                todayChallengeList.filter { it.status == "WAIT" }.sumOf { it.challenge.reward }
                    .priceComma()
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
            .padding(horizontal = 16.dp)
            .clip(CircleShape)
            .border(width = 1.dp, color = if (isSuccess) main15 else gray500, shape = CircleShape)
            .then(
                if (isSuccess) {
                    Modifier.background(
                        brush = Brush.horizontalGradient(listOf(main20, main15))
                    )
                } else {
                    Modifier.background(gray700)
                }
            )
            .noRippleClickable { onItemClick() }
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
        in (0 until 2000) -> context.getString(R.string.home_price_phrase_0)
        in (2000 until 5000) -> context.getString(R.string.home_price_phrase_1)
        in (5000 until 10000) -> context.getString(R.string.home_price_phrase_2)
        in (10000 until 20000) -> context.getString(R.string.home_price_phrase_3)
        in (20000 until 30000) -> context.getString(R.string.home_price_phrase_4)
        in (30000 until 40000) -> context.getString(R.string.home_price_phrase_5)
        in (40000 until 50000) -> context.getString(R.string.home_price_phrase_6)
        in (50090 until 100000) -> context.getString(R.string.home_price_phrase_7)
        else -> context.getString(R.string.home_price_phrase_8)
    }
}
