package com.project.presentation.challenge.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.project.domain.model.challenge.ChallengeRecordModel
import com.project.domain.model.user.WeeklyStatusModel
import com.project.presentation.R
import com.project.presentation.base.BaseBar
import com.project.presentation.base.BaseDatePickerBottomSheetContent
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTab
import com.project.presentation.base.extension.ComposeExtension.fadingEdge
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.base.extension.LocalDateExtension.toKoYmdFormatter
import com.project.presentation.challenge.addition.calculateScrimAlpha
import com.project.presentation.challenge.addition.getScreenHeightInPixelsOnce
import com.project.presentation.common.DayOfWeekEnum
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.main15
import com.project.presentation.ui.theme.main60
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(widthDp = 400)
fun ChallengeScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val weeklyDataMap = viewModel.weeklyDataMap
    val coroutineScope = rememberCoroutineScope()

    // 현재 화면에 해당하는 backStackEntry를 가져옴
    val currentBackStackEntry =
        remember { navHostController.getBackStackEntry(NavItem.Challenge.route) }
    // Lifecycle을 감지하여 ON_RESUME 이벤트에 반응
    DisposableEffect(currentBackStackEntry) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch {
                    viewModel.fetchThreeWeekStatus(
                        offset = (Int.MAX_VALUE / 2).toLong(),
                        targetDate = state.value.selectedDate,
                        isNew = true
                    )
                    viewModel.fetchSelectedDateChallengeList()
                }
            }
        }
        currentBackStackEntry.lifecycle.addObserver(observer)
        onDispose {
            currentBackStackEntry.lifecycle.removeObserver(observer)
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    var bottomSheetScrimAlpha by remember {
        mutableFloatStateOf(0f)
    }
    val isBottomSheetScrimVisible by remember {
        derivedStateOf {
            bottomSheetScrimAlpha > 0
        }
    }
    val screenHeight = getScreenHeightInPixelsOnce()

    val bottomSheetContentHeightInPx by remember {
        mutableFloatStateOf(434f)
    }
    val bottomSheetDragHandleHeightInPx by remember {
        mutableFloatStateOf(29f)
    }
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {

        BottomSheetScaffold(
            modifier = Modifier
                .drawBehind {
                    val offset = scaffoldState.bottomSheetState.requireOffset() // 바텀시트 현재 오프셋 값
                    val bottomSheetMinOffset =
                        screenHeight - bottomSheetContentHeightInPx - bottomSheetDragHandleHeightInPx // 바텀시트가 가질 수 있는 최소 offset 값
                    bottomSheetScrimAlpha = calculateScrimAlpha(
                        offset = offset,
                        screenHeight = screenHeight,
                        bottomSheetMinOffset = bottomSheetMinOffset
                    )
                },
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = transparent,
            sheetSwipeEnabled = false,
            sheetContent = {
                BaseDatePickerBottomSheetContent(
                    initYear = state.value.selectedDate.year.toString(),
                    initMonth = state.value.selectedDate.monthValue.toString(),
                    initDay = state.value.selectedDate.dayOfMonth.toString(),
                    yearList = (1900..state.value.todayDate.year + 1).map { it.toString() },
                    onConfirm = { selectedDate ->
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.partialExpand()
                            viewModel.onEvent(ChallengeEvent.ChangeSelectedDate(selectedDate))

                            val selectedWeekStart =
                                selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            val baseWeekStart = state.value.todayDate.with(
                                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                            )
                            val weekDiff =
                                ChronoUnit.WEEKS.between(baseWeekStart, selectedWeekStart).toInt()
                            val targetPage = Int.MAX_VALUE / 2 + weekDiff
                            viewModel.onEvent((ChallengeEvent.ChangePage(offset = weekDiff.toLong(), targetDate = selectedWeekStart)))
                            pagerState.scrollToPage(targetPage)
                        }
                    }
                )
            },
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(gray600)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                if (delta > 50f) {
                                    // 드래그 이동 처리
                                    coroutineScope.launch {
                                        scaffoldState.bottomSheetState.partialExpand()
                                    }
                                }
                            }
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp, 4.dp)
                            .clip(CircleShape)
                            .background(gray400)
                    )
                }
            },
            sheetContentColor = gray600,
            sheetContainerColor = gray600,
            content = {
                Scaffold(containerColor = transparent,
                    bottomBar = {
                        BaseBottomNavBar(navController = navHostController)
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ChallengeContent(
                            modifier = Modifier.fillMaxWidth(),
                            pagerState = pagerState,
                            state = state.value,
                            weeklyDataMap = weeklyDataMap,
                            challengeList = state.value.challengeList,
                            onAddClick = {
                                val route = NavItem.ChallengeAddition.route.replace(
                                    "{isOnboarding}",
                                    "false"
                                )
                                navHostController.navigate(route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onDateChange = { newDate ->
                                viewModel.onEvent(
                                    ChallengeEvent.ChangeSelectedDate(
                                        newDate
                                    )
                                )
                            },
                            onPageChanged = { offset, date ->
                                viewModel.onEvent(
                                    ChallengeEvent.ChangePage(
                                        offset = offset,
                                        targetDate = date
                                    )
                                )
                            },
                            onItemClick = {
                                val route = NavItem.ChallengeDetail.route.replace(
                                    "{challengeId}",
                                    "${it.challenge.id}"
                                )
                                navHostController.navigate(route)
                            },
                            onDateSelectorClick = {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        )

                        if (isBottomSheetScrimVisible) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .drawBehind {
                                        drawRect(black.copy(alpha = bottomSheetScrimAlpha))
                                    }
                                    .noRippleClickable { }
                            )
                        }
                    }
                }
            }
        )

    }
}

@Composable
fun ChallengeContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    state: ChallengeState,
    weeklyDataMap: Map<Long, List<WeeklyStatusModel>>,
    challengeList: List<ChallengeRecordModel>,
    onAddClick: () -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onPageChanged: (Long, LocalDate) -> Unit,
    onItemClick: (ChallengeRecordModel) -> Unit,
    onDateSelectorClick: () -> Unit
) {

    Column(modifier = modifier.fillMaxSize()) {
        ChallengeHeader(
            modifier = Modifier.fillMaxWidth(),
            isGroup = false,
            onIndividual = {},
            onGroup = {},
            onAddClick = onAddClick
        )
        // 선택된 일자를 표시
        ChallengeDateSelector(
            text = state.selectedDate.toKoYmdFormatter(),
            onDateSelectorClick = onDateSelectorClick
        )
        // 선택 일자와 업데이트 콜백을 전달
        ChallengeWeeklyCalendar(
            modifier = Modifier.fillMaxWidth(),
            selectedDate = state.selectedDate,
            todayDate = state.todayDate,
            pagerState = pagerState,
            weeklyDataMap = weeklyDataMap,
            onDateChange = onDateChange,
            onPageChanged = onPageChanged,
        )

        ChallengeListContent(
            modifier = Modifier.weight(1f),
            challengeList = challengeList,
            isToday = state.selectedDate.isEqual(state.todayDate),
            isBefore = state.selectedDate.isBefore(state.todayDate),
            onItemClick = onItemClick
        )
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
                modifier = Modifier.noRippleClickable { onIndividual() },
                text = stringResource(R.string.challenge_individual),
                style = if (isGroup) goolbitgTypography.h2 else goolbitgTypography.h1,
                color = if (isGroup) gray400 else white
            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                modifier = Modifier.clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null
//                ) { onGroup() },
//                text = stringResource(R.string.challenge_group),
//                style = if (isGroup) goolbitgTypography.h1 else goolbitgTypography.h2,
//                color = if (isGroup) white else gray400
//            )
        }
        BaseIcon(
            modifier = Modifier
                .noRippleClickable {
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
    onDateSelectorClick: () -> Unit
) {
    Row(
        modifier = modifier
            .noRippleClickable {
                onDateSelectorClick()
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = goolbitgTypography.body1, color = gray50)
        Spacer(modifier = Modifier.width(8.dp))
        BaseIcon(iconId = R.drawable.ic_arrow_down)
    }
}


@Composable
fun ChallengeWeeklyCalendar(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    selectedDate: LocalDate,
    todayDate: LocalDate,
    weeklyDataMap: Map<Long, List<WeeklyStatusModel>>,
    onDateChange: (LocalDate) -> Unit,
    onPageChanged: (Long, LocalDate) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
    ) { page ->
        // 현재 페이지에서의 시작 날짜 계산
        val currentWeekStart = todayDate.plusDays((page - Int.MAX_VALUE / 2) * 7L)
        val weekDates = getWeekDates(currentWeekStart)
        val weeklyStatusList = weeklyDataMap[page.toLong()]

        // 페이지가 변경될 때마다 weekOffset 계산 후 onPageChanged 콜백 호출
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .collect { page ->
                    onPageChanged(page.toLong(), currentWeekStart)
                }
        }

        Row(
            modifier = Modifier
                .height(100.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            weekDates.forEachIndexed { idx, date ->
                val weeklyStatus = weeklyStatusList?.get(idx)
                val progress = if (weeklyStatus != null && weeklyStatus.totalChallenges > 0) {
                    weeklyStatus.achievedChallenges.toFloat() / weeklyStatus.totalChallenges.toFloat() * 100
                } else {
                    0f
                }
                if (idx != 0) {
                    val prevWeeklyStatus = weeklyStatusList?.get(idx - 1)
                    val isPrevProgressFulled = if (prevWeeklyStatus != null) {
                        prevWeeklyStatus.totalChallenges != 0 &&
                                (prevWeeklyStatus.achievedChallenges == prevWeeklyStatus.totalChallenges)
                    } else {
                        false
                    }
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .fillMaxHeight()
                    ) {
                        if (isPrevProgressFulled && progress > 0f) {
                            BaseIcon(
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.BottomCenter),
                                iconId = R.drawable.img_challenge_continuous
                            )
                        }
                    }

                }
                CalendarItem(
                    modifier = Modifier.weight(40f),
                    dayOfWeek = date.dayOfWeek.value,
                    day = date.dayOfMonth.toString(),
                    progress = progress,
                    isSelected = date.isEqual(selectedDate),
                    isToday = date.isEqual(todayDate),
                    isAfter = date.isAfter(todayDate),
                    onClick = {
                        onDateChange(date)
                    }
                )
            }
        }
    }
}

@Composable
fun CalendarItem(
    day: String,
    dayOfWeek: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isToday: Boolean = false,
    isAfter: Boolean = false,
    onClick: () -> Unit = {}
) {
    val ratio = 40f / 111f
    // 요일 문자열은 기존 리소스 사용
    val dayOfWeekStr = stringResource(DayOfWeekEnum.entries[dayOfWeek - 1].strId2)
    Column(
        modifier = modifier
            .aspectRatio(ratio)
            .noRippleClickable { onClick() }
    ) {
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
                    .then(
                        if (isSelected) {
                            Modifier
                                .clip(CircleShape)
                                .background(main60)
                        } else {
                            if (isToday) {
                                Modifier
                                    .clip(CircleShape)
                                    .border(width = 1.dp, color = gray500, shape = CircleShape)
                                    .background(gray600)
                            } else {
                                Modifier
                            }
                        }
                    )
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    text = day,
                    style = goolbitgTypography.body1,
                    color = if (isSelected || !isAfter) white else gray500
                )
            }
            Box(modifier = Modifier.weight(16f)) {
                BaseBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.Center),
                    progress = progress
                )
            }
        }
        Spacer(modifier = Modifier.weight(8f))
    }
}

@Composable
fun ChallengeListContent(
    modifier: Modifier = Modifier,
    challengeList: List<ChallengeRecordModel>,
    isToday: Boolean,
    isBefore: Boolean,
    onItemClick: (ChallengeRecordModel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var selectedTabIdx by rememberSaveable { mutableIntStateOf(0) }
        val filteredChallengeList by remember(selectedTabIdx, challengeList) {
            mutableStateOf(
                challengeList.challengeFilter(
                    isToday = isToday,
                    isComplete = selectedTabIdx == 1
                )
            )
        }
        if (isToday) {
            BaseTab(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(247.dp, 41.dp),
                items = listOf("진행 중", "진행 완료"),
                selectedItemIndex = selectedTabIdx,
                onSelectedTab = { selectedTabIdx = it }
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

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

        if (filteredChallengeList.isNotEmpty()) {
            LazyColumn(
                modifier = modifier
                    .weight(1f)
                    .then(
                        if (listFade != null) {
                            Modifier.fadingEdge(listFade!!)
                        } else {
                            Modifier
                        }
                    ),
                state = listState
            ) {
                items(items = filteredChallengeList) {
                    ChallengeListItem(
                        item = it,
                        isBefore = isBefore,
                        onItemClick = onItemClick
                    )
                }
            }
        } else {
            ChallengeListEmpty(isBefore = isBefore)
        }
    }
}

@Composable
fun ChallengeListEmpty(
    modifier: Modifier = Modifier,
    isBefore: Boolean
) {
    if (isBefore) {
        Spacer(modifier = Modifier.height(41.dp))
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BaseIcon(iconId = R.drawable.ic_empty_challenge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(if (!isBefore) R.string.challenge_empty_2 else R.string.challenge_empty_1),
            style = goolbitgTypography.body2,
            color = gray300,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ChallengeListItem(
    modifier: Modifier = Modifier,
    item: ChallengeRecordModel,
    isBefore: Boolean,
    onItemClick: (ChallengeRecordModel) -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .noRippleClickable {
                    if (!isBefore) {
                        onItemClick(item)
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                modifier = Modifier.size(45.dp),
                model = item.challenge.imageUrlSmall,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = item.challenge.title, style = goolbitgTypography.body3, color = white)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.challenge_continuous_day)
                        .replace("#VALUE#", item.duration.toString()),
                    style = goolbitgTypography.body5,
                    color = white
                )
            }
            if (!isBefore) {
                BaseIcon(iconId = R.drawable.ic_arrow_right_over)
            } else {
                val isSuccess = item.status == "SUCCESS"
                val strId = if (isSuccess) R.string.common_success else R.string.common_fail
                val bgColor = if (isSuccess) main15 else white.copy(alpha = 0.1f)
                val textColor = if (isSuccess) main100 else gray300
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgColor)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    text = stringResource(strId),
                    color = textColor,
                    style = goolbitgTypography.body5.copy(fontWeight = FontWeight.Bold)
                )
            }
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

fun getWeekDates(startDate: LocalDate): List<LocalDate> {
    val startOfWeek = startDate.with(DayOfWeek.MONDAY) // 시작 날짜를 월요일로 고정
    return (0 until 7).map { startOfWeek.plusDays(it.toLong()) }
}

fun List<ChallengeRecordModel>.challengeFilter(
    isToday: Boolean,
    isComplete: Boolean = false
): List<ChallengeRecordModel> {
    return if (isToday) {
        if (isComplete) {
            this.filter { it.status == "SUCCESS" }
        } else {
            this.filter { it.status != "SUCCESS" }
        }
    } else {
        this
    }
}