package com.project.presentation.challengegroup.main

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import com.valentinilk.shimmer.shimmer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.domain.model.challengegroup.ChallengeGroupModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.white

// ─── Stateful ────────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeGroupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // 다른 화면에서 돌아왔을 때 (예: 설정에서 그룹 삭제/나가기 후) 자동 새로고침
    val backStackEntry = remember { navHostController.getBackStackEntry(NavItem.ChallengeGroupMain.route) }
    DisposableEffect(backStackEntry) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(ChallengeGroupEvent.LoadGroupList)
            }
        }
        backStackEntry.lifecycle.addObserver(observer)
        onDispose { backStackEntry.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = bg1,
        bottomBar = { BaseBottomNavBar(navController = navHostController) }
    ) { innerPadding ->
        ChallengeGroupContent(
            state = state,
            modifier = Modifier.padding(innerPadding),
            onEvent = viewModel::onEvent,
            onNavigateToIndividual = {
                // 그룹 → 개인 전환은 같은 탭 내 토글이므로 백스택을 교체
                navHostController.navigate(NavItem.Challenge.route) {
                    popUpTo(NavItem.ChallengeGroupMain.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onNavigateToSearch = { navHostController.navigate(NavItem.ChallengeGroupSearch.route) },
            onNavigateToCreate = { navHostController.navigate(NavItem.ChallengeGroupCreate.route) },
            onNavigateToDetail = { groupId ->
                navHostController.navigate(
                    NavItem.ChallengeGroupDetail.route.replace("{groupId}", "$groupId")
                )
            }
        )
    }
}

// ─── Stateless ───────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupContent(
    state: ChallengeGroupState,
    onEvent: (ChallengeGroupEvent) -> Unit,
    onNavigateToIndividual: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ChallengeGroupHeader(
            isMyRoomOnly = state.isMyRoomOnly,
            onIndividual = onNavigateToIndividual,
            onSearchClick = onNavigateToSearch,
            onCreateClick = onNavigateToCreate,
            onToggleMyRoom = { onEvent(ChallengeGroupEvent.ToggleMyRoomOnly(!state.isMyRoomOnly)) }
        )
        when {
            state.isLoading -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(count = 6) { ChallengeGroupItemSkeleton() }
            }
            state.groupList.isEmpty() -> ChallengeGroupEmpty()
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                // key 지정 → 리스트 변경 시 불필요한 아이템 recomposition 방지
                items(items = state.groupList, key = { it.id }) { group ->
                    ChallengeGroupItem(
                        group = group,
                        onClick = { onNavigateToDetail(group.id) }
                    )
                }
            }
        }
    }
}

// ─── Sub-components (Stateless) ──────────────────────────────────────────────
@Composable
fun ChallengeGroupHeader(
    isMyRoomOnly: Boolean,
    onIndividual: () -> Unit,
    onSearchClick: () -> Unit,
    onCreateClick: () -> Unit,
    onToggleMyRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.noRippleClickable { onIndividual() },
                    text = "개인",
                    style = goolbitgTypography.h2,
                    color = gray400
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "그룹",
                    style = goolbitgTypography.h1,
                    color = white
                )
            }
            BaseIcon(
                modifier = Modifier
                    .noRippleClickable { onCreateClick() }
                    .padding(8.dp),
                iconId = R.drawable.ic_plus
            )
            Spacer(modifier = Modifier.width(4.dp))
            BaseIcon(
                modifier = Modifier
                    .noRippleClickable { onSearchClick() }
                    .padding(8.dp),
                iconId = R.drawable.ic_search
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "참여중인 작심삼일 방",
                style = goolbitgTypography.body2,
                color = white
            )
            Row(
                modifier = Modifier.noRippleClickable { onToggleMyRoom() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✓",
                    style = goolbitgTypography.body4,
                    color = if (isMyRoomOnly) main100 else gray400
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "내가 만든 방만 보기",
                    style = goolbitgTypography.body5,
                    color = if (isMyRoomOnly) gray100 else gray400
                )
            }
        }
    }
}

@Composable
fun ChallengeGroupItem(
    group: ChallengeGroupModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // 제목 + (선택적) 자물쇠 아이콘
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.title,
                        style = goolbitgTypography.body2,
                        color = white,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (group.isHidden) {
                        Spacer(modifier = Modifier.width(6.dp))
                        BaseIcon(
                            modifier = Modifier.size(20.dp),
                            iconId = R.drawable.ic_lock_circled
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // 메타: 인원 · 카테고리 · 해시태그
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BaseIcon(
                        modifier = Modifier.size(14.dp),
                        iconId = R.drawable.ic_users_color
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${group.peopleCount}/${group.maxSize}",
                        style = goolbitgTypography.body5,
                        color = main100
                    )
                    if (group.category.isNotBlank()) {
                        Text(
                            text = " · ",
                            style = goolbitgTypography.body5,
                            color = gray400
                        )
                        CategoryChip(text = group.category)
                    }
                    if (group.hashtags.isNotEmpty()) {
                        Text(
                            text = " · ",
                            style = goolbitgTypography.body5,
                            color = gray400
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = group.hashtags.joinToString(" ") { "#$it" },
                            style = goolbitgTypography.body5,
                            color = gray400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            BaseIcon(
                modifier = Modifier.size(16.dp),
                iconId = R.drawable.ic_arrow_right_16
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(gray600)
        )
    }
}

@Composable
private fun CategoryChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(gray500)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = goolbitgTypography.body5,
            color = gray100
        )
    }
}

@Composable
fun ChallengeGroupItemSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // 제목 자리
        Box(
            modifier = Modifier
                .size(width = 160.dp, height = 16.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
                .background(white.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.height(8.dp))
        // 메타정보 (참여중 + 해시태그) 자리
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
                .background(white.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(gray600)
        )
    }
}

@Composable
fun ChallengeGroupEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "참여중인 작심삼일 방이 없어요",
            style = goolbitgTypography.body2,
            color = gray300
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "방을 만들거나 검색해서 참여해 보세요",
            style = goolbitgTypography.body5,
            color = gray400
        )
    }
}

// ─── Preview (Stateless 버전 사용) ──────────────────────────────────────────
@Preview(widthDp = 400)
@Composable
private fun ChallengeGroupContentPreview() {
    ChallengeGroupContent(
        state = ChallengeGroupState(
            allGroups = listOf(
                ChallengeGroupModel(
                    id = 1, ownerId = "user1", title = "커피 끊기 챌린지",
                    peopleCount = 3, maxSize = 10, isHidden = false,
                    hashtags = listOf("절약", "커피"), reward = 30000,
                    category = "커피", password = null,
                    avgAchieveRatio = 0.7f, maxAchieveDays = 3
                )
            )
        ),
        onEvent = {},
        onNavigateToIndividual = {},
        onNavigateToSearch = {},
        onNavigateToCreate = {},
        onNavigateToDetail = {}
    )
}
