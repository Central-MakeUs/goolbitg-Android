package com.project.presentation.challengegroup.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.SheetValue
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.project.domain.model.ChallengeStatus
import com.project.domain.model.challengegroup.ChallengeGroupModel
import com.project.domain.model.challengegroup.ChallengeGroupRankItemModel
import com.project.domain.model.challengegroup.ChallengeGroupRankModel
import com.project.domain.model.challengegroup.ChallengeGroupTrippleModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.white

private val PodiumGreenStart = Color(0xFF41A420)
private val PodiumGreenEnd = Color(0xFF67BF4E)
private val SavingGreen = Color(0xFF4BB329)
private const val MIN_PARTICIPANTS = 3
private val HeaderHeight = 64.dp
private val PodiumHeight = 200.dp

// ─── Stateful ────────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupDetailScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeGroupDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { navHostController.popBackStack() }
    }

    val group = state.rankModel?.group
    val title = group?.title.orEmpty()
    val isEmpty = group != null && group.peopleCount < MIN_PARTICIPANTS

    val onSettings: (Int) -> Unit = { groupId ->
        navHostController.navigate(
            NavItem.ChallengeGroupSettings.route.replace("{groupId}", "$groupId")
        )
    }

    if (group == null || isEmpty) {
        ChallengeGroupDetailEmptyContent(
            title = title,
            groupId = state.groupId,
            onBack = { navHostController.popBackStack() },
            onNavigateToSettings = onSettings
        )
    } else {
        ChallengeGroupDetailContent(
            rankModel = state.rankModel!!,
            tripple = state.tripple,
            onBack = { navHostController.popBackStack() },
            onCheckToday = { viewModel.onEvent(ChallengeGroupDetailEvent.CheckToday) },
            onNavigateToSettings = onSettings
        )
    }
}

// ─── Stateless: 정상(3명 이상) ───────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeGroupDetailContent(
    rankModel: ChallengeGroupRankModel,
    tripple: ChallengeGroupTrippleModel?,
    onBack: () -> Unit,
    onCheckToday: () -> Unit,
    onNavigateToSettings: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val group = rankModel.group
    val ranks = rankModel.rank
    val top3 = ranks.take(3)
    val rest = if (ranks.size > 3) ranks.drop(3) else emptyList()

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    val density = LocalDensity.current
    val headerHeightPx = with(density) { HeaderHeight.toPx() }
    val podiumHeightPx = with(density) { PodiumHeight.toPx() }

    // 시상대 collapse 양 (0f = 펼침, -podiumHeightPx = 완전 접힘)
    // Animatable 로 두 끝값 사이만 오가도록 → 어중간한 상태 없이 snap
    val podiumAnim = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val collapseProgress by remember(podiumHeightPx) {
        derivedStateOf { (-podiumAnim.value / podiumHeightPx).coerceIn(0f, 1f) }
    }

    val nestedScroll = remember(podiumHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 위로 스크롤 시작 + 시상대가 펼쳐진 상태 → 즉시 완전 접힘으로 애니메이션
                if (available.y < 0f && podiumAnim.value > -podiumHeightPx) {
                    coroutineScope.launch { podiumAnim.animateTo(-podiumHeightPx) }
                    return available
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // 리스트 맨 위에서 더 아래로 당김 → 시상대 다시 펼침
                if (available.y > 0f && podiumAnim.value < 0f) {
                    coroutineScope.launch { podiumAnim.animateTo(0f) }
                    return available
                }
                return Offset.Zero
            }
        }
    }

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 36.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContainerColor = gray600,
        sheetContentColor = white,
        sheetDragHandle = { ChallengeGroupDragHandle() },
        sheetContent = {
            ChallengeGroupBottomSheetContent(
                group = group,
                tripple = tripple,
                onCheckToday = onCheckToday
            )
        },
        containerColor = bg1
    ) { innerPadding ->
        // collapseProgress 에 따라 헤더 배경색을 시상대 그린 → 리스트 다크로 보간
        val headerBg = lerp(PodiumGreenStart, bg1, collapseProgress)
        // 현재 시상대가 차지하는 dp 높이 (리스트 contentPadding 동적 계산용)
        val currentPodiumDp = with(density) {
            (podiumHeightPx + podiumAnim.value).coerceAtLeast(0f).toDp()
        }
        // 시상대 ↔ 리스트 사이 16dp 간격 (시상대가 접히면 비례해서 줄어듦)
        val gapDp = 16.dp * (1f - collapseProgress)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(nestedScroll)
        ) {
            // 1) 랭킹 리스트 — 헤더 아래부터 그려지며, contentPadding 의 top 이
            //    시상대 잔여 높이만큼이라 시상대가 위로 접히면 리스트도 같이 따라 올라옴
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = HeaderHeight)
                    .background(bg1),
                contentPadding = PaddingValues(
                    top = currentPodiumDp + gapDp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(rest, key = { idx, item -> "${idx + 4}-${item.name}" }) { idx, item ->
                    RankRowItem(rank = idx + 4, item = item)
                }
            }

            // 2) 시상대 — 헤더 아래에 떠있고, 스크롤에 따라 위로 접힘
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PodiumHeight)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (headerHeightPx + podiumAnim.value).toInt()
                        )
                    }
                    .graphicsLayer { alpha = 1f - collapseProgress }
                    .clipToBounds()
            ) {
                ChallengeGroupPodium(top3 = top3)
            }

            // 3) 그린 헤더 — 항상 최상단 고정, 시상대 색에서 시작해 리스트 배경색으로 보간
            //    부모 Box 가 이미 BottomSheetScaffold 의 innerPadding(systemBars) 을 적용했으므로
            //    헤더의 windowInsets 은 0 으로 override 하여 이중 padding 을 방지
            ChallengeGroupGreenHeader(
                modifier = Modifier.align(Alignment.TopCenter),
                title = group.title,
                backgroundColor = headerBg,
                onBack = onBack,
                onSettings = { onNavigateToSettings(group.id) },
                windowInsets = WindowInsets(0)
            )
        }
    }
}

// ─── Stateless: 빈 상태 (3명 미만) ────────────────────────────────────────────
@Composable
fun ChallengeGroupDetailEmptyContent(
    title: String,
    groupId: Int,
    onBack: () -> Unit,
    onNavigateToSettings: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Empty 화면은 Scaffold 미사용 → 헤더는 자체 statusBarsPadding 으로 처리되고,
    // 본문 영역은 navigationBarsPadding 으로 하단 시스템바 회피
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        ChallengeGroupDarkHeader(
            title = title,
            onBack = onBack,
            onSettings = { onNavigateToSettings(groupId) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BaseIcon(
                    modifier = Modifier.size(180.dp),
                    iconId = R.drawable.ic_empty_challenge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "최소 3명이 모여야해요!",
                    style = goolbitgTypography.body2,
                    color = gray300
                )
            }
        }
    }
}

// ─── Headers ─────────────────────────────────────────────────────────────────
@Composable
private fun ChallengeGroupGreenHeader(
    title: String,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = main100,
    windowInsets: WindowInsets = WindowInsets.statusBars
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .windowInsetsPadding(windowInsets)
            .height(HeaderHeight)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(
            modifier = Modifier
                .size(32.dp)
                .noRippleClickable { onBack() },
            iconId = R.drawable.ic_arrow_left
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = goolbitgTypography.h4,
            color = white,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        BaseIcon(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .noRippleClickable { onSettings() }
                .padding(10.dp),
            iconId = R.drawable.ic_settings
        )
    }
}

@Composable
private fun ChallengeGroupDarkHeader(
    title: String,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    windowInsets: WindowInsets = WindowInsets.statusBars
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg1)
            .windowInsetsPadding(windowInsets)
            .height(HeaderHeight)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(
            modifier = Modifier
                .size(32.dp)
                .noRippleClickable { onBack() },
            iconId = R.drawable.ic_arrow_left
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = goolbitgTypography.h4,
            color = white,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        BaseIcon(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .noRippleClickable { onSettings() }
                .padding(10.dp),
            iconId = R.drawable.ic_settings
        )
    }
}

// ─── Podium (시상대) ─────────────────────────────────────────────────────────
@Composable
private fun ChallengeGroupPodium(
    top3: List<ChallengeGroupRankItemModel>
) {
    val first = top3.getOrNull(0)
    val second = top3.getOrNull(1)
    val third = top3.getOrNull(2)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(listOf(PodiumGreenStart, PodiumGreenEnd)),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 40.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 2등 (좌)
            PodiumColumn(
                item = second,
                rank = 2,
                pedestalHeight = 64.dp,
                modifier = Modifier.weight(1f)
            )
            // 1등 (중앙) — 가장 높은 단상
            PodiumColumn(
                item = first,
                rank = 1,
                pedestalHeight = 91.dp,
                modifier = Modifier.weight(1f)
            )
            // 3등 (우)
            PodiumColumn(
                item = third,
                rank = 3,
                pedestalHeight = 46.dp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PodiumColumn(
    item: ChallengeGroupRankItemModel?,
    rank: Int,
    pedestalHeight: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 프로필 + 닉네임 + 금액 뱃지
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(gray500),
            contentAlignment = Alignment.Center
        ) {
            BaseIcon(
                modifier = Modifier.size(54.dp),
                iconId = R.drawable.ic_mypage_profile_default
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item?.name.orEmpty(),
            style = goolbitgTypography.body3,
            color = white,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(Color.White.copy(alpha = 0.3f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${formatAmount(item?.saving ?: 0)}원",
                style = goolbitgTypography.caption3.copy(fontSize = 8.sp),
                color = white,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // 단상 (frosted glass 느낌)
        val pedestalAlpha = if (rank == 1) 0.30f else 0.24f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(Color.White.copy(alpha = pedestalAlpha))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                )
        )
    }
}

// ─── 4등 이하 랭킹 행 ────────────────────────────────────────────────────────
@Composable
private fun RankRowItem(
    rank: Int,
    item: ChallengeGroupRankItemModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(100.dp))
            .background(gray700)
            .border(1.dp, gray500, RoundedCornerShape(100.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(32.dp),
            text = "$rank",
            style = goolbitgTypography.h3,
            color = gray200,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(gray500),
            contentAlignment = Alignment.Center
        ) {
            BaseIcon(
                modifier = Modifier.size(54.dp),
                iconId = R.drawable.ic_mypage_profile_default
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = item.name,
            style = goolbitgTypography.body3,
            color = white,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "+${formatAmount(item.saving)}원",
            style = goolbitgTypography.body3,
            color = SavingGreen,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─── BottomSheet ─────────────────────────────────────────────────────────────
@Composable
private fun ChallengeGroupDragHandle() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(gray500)
        )
    }
}

@Composable
private fun ChallengeGroupBottomSheetContent(
    group: ChallengeGroupModel,
    tripple: ChallengeGroupTrippleModel?,
    onCheckToday: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 챌린지명
        Text(
            text = group.title,
            style = goolbitgTypography.h3,
            color = gray50,
            textAlign = TextAlign.Center
        )
        // 해시태그
        if (group.hashtags.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = group.hashtags.joinToString(" ") { "#$it" },
                style = goolbitgTypography.body5,
                color = white,
                textAlign = TextAlign.Center
            )
        }
        // 절약 정보
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${group.maxAchieveDays}일 연속 성공 시 ",
                style = goolbitgTypography.body4,
                color = gray200
            )
            Text(
                text = "${formatAmount(group.reward)}원 절약",
                style = goolbitgTypography.body3,
                color = gray200
            )
        }
        // 참여 인원
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${group.peopleCount}/${group.maxSize} 참여 완료",
                style = goolbitgTypography.body3,
                color = SavingGreen
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // 1·2·3일차 체크 모듈
        DayCheckRow(
            tripple = tripple,
            onCheckToday = onCheckToday
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DayCheckRow(
    tripple: ChallengeGroupTrippleModel?,
    onCheckToday: () -> Unit
) {
    val checks = listOf(
        tripple?.check1.orEmpty(),
        tripple?.check2.orEmpty(),
        tripple?.check3.orEmpty()
    )
    val location = tripple?.location ?: 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        checks.forEachIndexed { idx, status ->
            val day = idx + 1
            val isChecked = status == ChallengeStatus.SUCCESS
            val isCurrent = day == location && !isChecked
            DayCheckCell(
                day = day,
                isChecked = isChecked,
                isCurrent = isCurrent,
                onClick = if (isCurrent) onCheckToday else null
            )
        }
    }
}

@Composable
private fun DayCheckCell(
    day: Int,
    isChecked: Boolean,
    isCurrent: Boolean,
    onClick: (() -> Unit)?
) {
    val iconRes = when {
        isChecked -> R.drawable.ic_challenge_detail_checked
        isCurrent -> R.drawable.ic_challenge_detail_enabled
        else -> R.drawable.ic_challenge_detail_disabled
    }
    val labelColor = when {
        isChecked -> gray200
        isCurrent -> gray200
        else -> gray400
    }
    Column(
        modifier = Modifier
            .height(80.dp)
            .let { if (onClick != null) it.noRippleClickable { onClick() } else it },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        BaseIcon(
            modifier = Modifier.size(56.dp),
            iconId = iconRes
        )
        Text(
            text = "${day}일차",
            style = goolbitgTypography.body4,
            color = labelColor
        )
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────
private fun formatAmount(value: Int): String {
    if (value == 0) return "0"
    val sign = if (value < 0) "-" else ""
    val absValue = kotlin.math.abs(value)
    val s = absValue.toString()
    val sb = StringBuilder()
    val len = s.length
    for (i in 0 until len) {
        if (i > 0 && (len - i) % 3 == 0) sb.append(',')
        sb.append(s[i])
    }
    return "$sign$sb"
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun DetailContentPreview() {
    val fakeGroup = ChallengeGroupModel(
        id = 1, ownerId = "user1", title = "{챌린지명}", reward = 15000,
        hashtags = listOf("절약", "커피", "다이어트"), category = "커피", maxSize = 10,
        peopleCount = 8, isHidden = false, password = null,
        avgAchieveRatio = 0.8f, maxAchieveDays = 3
    )
    val fakeTripple = ChallengeGroupTrippleModel(
        duration = 1, check1 = ChallengeStatus.WAIT, check2 = ChallengeStatus.WAIT,
        check3 = ChallengeStatus.WAIT, location = 1, canceled = false
    )
    val fakeRank = listOf(
        ChallengeGroupRankItemModel("{닉네임}", "", 1_200_000),
        ChallengeGroupRankItemModel("룽지쓰", "", 850_000),
        ChallengeGroupRankItemModel("굴비즈", "", 400_000),
        ChallengeGroupRankItemModel("{닉네임}", "", 100_000),
        ChallengeGroupRankItemModel("절약굴비", "", 90_000),
        ChallengeGroupRankItemModel("짠돌굴비", "", 80_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
        ChallengeGroupRankItemModel("동워니", "", 60_000),
    )
    ChallengeGroupDetailContent(
        rankModel = ChallengeGroupRankModel(group = fakeGroup, rank = fakeRank),
        tripple = fakeTripple,
        onBack = {},
        onCheckToday = {},
        onNavigateToSettings = {}
    )
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun DetailEmptyContentPreview() {
    ChallengeGroupDetailEmptyContent(
        title = "{챌린지명}",
        groupId = 1,
        onBack = {},
        onNavigateToSettings = {}
    )
}
