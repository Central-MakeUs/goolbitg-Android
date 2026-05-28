package com.project.presentation.challengegroup.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseHeader
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTwoButtonPopup
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.error
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.white

// ─── Stateful ────────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupSettingsScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeGroupSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect {
            // 삭제/나가기 성공 → 설정+상세 화면을 모두 pop, 메인 화면으로 복귀
            navHostController.popBackStack(
                route = NavItem.ChallengeGroupMain.route,
                inclusive = false
            )
        }
    }

    Scaffold(
        containerColor = bg1,
        topBar = {
            BaseHeader(
                title = "작심삼일 방 설정",
                onBackPressed = { navHostController.popBackStack() }
            )
        }
    ) { innerPadding ->
        ChallengeGroupSettingsContent(
            state = state,
            modifier = Modifier.padding(innerPadding),
            onToggleAlarm = { viewModel.onEvent(ChallengeGroupSettingsEvent.ToggleAlarm(it)) },
            onNavigateToInfo = {
                navHostController.navigate(
                    NavItem.ChallengeGroupSettingsInfo.route.replace("{groupId}", "${state.groupId}")
                )
            },
            onDeleteRequest = { showDeleteDialog = true },
            onExitRequest = { showExitDialog = true }
        )
    }

    if (showDeleteDialog) {
        BaseTwoButtonPopup(
            title = "작심삼일 방 삭제",
            subTitle = "작심삼일 방을\n정말 삭제하시겠어요?",
            confirmText = "삭제",
            dismissText = "취소",
            confirmBgColor = error,
            confirmTextColor = white,
            dismissBgColor = gray400,
            dismissTextColor = gray100,
            onConfirm = {
                showDeleteDialog = false
                viewModel.onEvent(ChallengeGroupSettingsEvent.DeleteGroup)
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showExitDialog) {
        BaseTwoButtonPopup(
            title = "작심삼일 방 나가기",
            subTitle = "작심삼일 방을\n정말 나가시겠어요?",
            confirmText = "나가기",
            dismissText = "취소",
            confirmBgColor = error,
            confirmTextColor = white,
            dismissBgColor = gray400,
            dismissTextColor = gray100,
            onConfirm = {
                showExitDialog = false
                viewModel.onEvent(ChallengeGroupSettingsEvent.ExitGroup)
            },
            onDismiss = { showExitDialog = false }
        )
    }
}

// ─── Stateless ───────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupSettingsContent(
    state: ChallengeGroupSettingsState,
    onToggleAlarm: (Boolean) -> Unit,
    onNavigateToInfo: () -> Unit,
    onDeleteRequest: () -> Unit,
    onExitRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 정보 수정 진입 row
        SettingsRow(
            label = "작심삼일 방 정보 수정",
            onClick = onNavigateToInfo
        ) {
            BaseIcon(
                modifier = Modifier
                    .size(20.dp)
                    .rotate(180f),
                iconId = R.drawable.ic_arrow_right_16
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(gray500)
        )
        // 알림 설정 토글
        SettingsRow(label = "알림 설정") {
            Switch(
                checked = state.isAlarmOn,
                onCheckedChange = onToggleAlarm,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = white,
                    checkedTrackColor = main100,
                    uncheckedThumbColor = gray300,
                    uncheckedTrackColor = gray600
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (state.isOwner) {
            // 생성자: 삭제 버튼 (참여자 ≥ 1 이면 비활성)
            DeleteButton(
                enabled = state.canDelete,
                onClick = onDeleteRequest
            )
            Spacer(modifier = Modifier.height(12.dp))
            DeleteWarning()
        } else {
            // 참여자: 나가기 버튼
            ExitButton(onClick = onExitRequest)
        }
    }
}

// ─── Sub-components ──────────────────────────────────────────────────────────
@Composable
private fun SettingsRow(
    label: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.noRippleClickable { onClick() } else it }
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            style = goolbitgTypography.caption1,
            color = white
        )
        Spacer(modifier = Modifier.width(9.dp))
        trailing()
    }
}

@Composable
private fun DeleteButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (enabled) gray400 else gray500
    val textColor = if (enabled) gray300 else gray500
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .let { if (enabled) it.noRippleClickable { onClick() } else it }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "작심삼일 삭제하기",
            style = goolbitgTypography.btn3,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ExitButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, gray400, RoundedCornerShape(24.dp))
            .noRippleClickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "작심삼일 나가기",
            style = goolbitgTypography.btn3,
            color = gray300,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DeleteWarning() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // warning 아이콘 자리 (에셋 추가 시 교체) - 작은 원형 placeholder
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .border(1.dp, gray300, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "!", style = goolbitgTypography.caption3, color = gray300)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "참여자가 1명이라도 있는 경우, 삭제가 불가합니다",
            style = goolbitgTypography.body5,
            color = gray300
        )
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(widthDp = 390)
@Composable
private fun OwnerWithParticipantsPreview() {
    ChallengeGroupSettingsContent(
        state = ChallengeGroupSettingsState(isOwner = true).copy(
            group = com.project.domain.model.challengegroup.ChallengeGroupModel(
                id = 1, ownerId = "u1", title = "t", reward = 0, hashtags = emptyList(),
                category = "c", maxSize = 10, peopleCount = 5, isHidden = false,
                password = null, avgAchieveRatio = 0f, maxAchieveDays = 0
            )
        ),
        onToggleAlarm = {}, onNavigateToInfo = {}, onDeleteRequest = {}, onExitRequest = {}
    )
}

@Preview(widthDp = 390)
@Composable
private fun OwnerSoloPreview() {
    ChallengeGroupSettingsContent(
        state = ChallengeGroupSettingsState(isOwner = true).copy(
            group = com.project.domain.model.challengegroup.ChallengeGroupModel(
                id = 1, ownerId = "u1", title = "t", reward = 0, hashtags = emptyList(),
                category = "c", maxSize = 10, peopleCount = 1, isHidden = false,
                password = null, avgAchieveRatio = 0f, maxAchieveDays = 0
            )
        ),
        onToggleAlarm = {}, onNavigateToInfo = {}, onDeleteRequest = {}, onExitRequest = {}
    )
}

@Preview(widthDp = 390)
@Composable
private fun ParticipantPreview() {
    ChallengeGroupSettingsContent(
        state = ChallengeGroupSettingsState(isOwner = false),
        onToggleAlarm = {}, onNavigateToInfo = {}, onDeleteRequest = {}, onExitRequest = {}
    )
}
