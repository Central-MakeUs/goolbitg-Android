package com.project.presentation.challengegroup.settings.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseHeader
import com.project.presentation.base.BaseTextField
import com.project.presentation.challengegroup.create.GroupFormLabel
import com.project.presentation.challengegroup.create.HiddenRoomToggleRow
import com.project.presentation.ui.theme.bg1

// ─── Stateful ────────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupSettingsInfoScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeGroupSettingsInfoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { navHostController.popBackStack() }
    }

    Scaffold(
        containerColor = bg1,
        topBar = {
            BaseHeader(
                title = "작심삼일 방 정보 수정",
                onBackPressed = { navHostController.popBackStack() }
            )
        }
    ) { innerPadding ->
        ChallengeGroupSettingsInfoContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// ─── Stateless ───────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupSettingsInfoContent(
    state: ChallengeGroupSettingsInfoState,
    onEvent: (ChallengeGroupSettingsInfoEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        GroupFormLabel("챌린지 이름 *")
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.title,
            placeHolderValue = "챌린지 이름을 입력하세요",
            onTextChanged = { onEvent(ChallengeGroupSettingsInfoEvent.ChangeTitle(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        GroupFormLabel("절약 목표 금액 *")
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.reward,
            placeHolderValue = "목표 절약 금액 (원)",
            onTextChanged = { onEvent(ChallengeGroupSettingsInfoEvent.ChangeReward(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))
        GroupFormLabel("카테고리 *")
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.category,
            placeHolderValue = "예: 커피, 배달, 택시",
            onTextChanged = { onEvent(ChallengeGroupSettingsInfoEvent.ChangeCategory(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        GroupFormLabel("해시태그")
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.hashtags,
            placeHolderValue = "#해시태그1 #해시태그2",
            onTextChanged = { onEvent(ChallengeGroupSettingsInfoEvent.ChangeHashtags(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        GroupFormLabel("최대 인원")
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.maxSize,
            placeHolderValue = "최대 참여 인원",
            onTextChanged = { onEvent(ChallengeGroupSettingsInfoEvent.ChangeMaxSize(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))
        HiddenRoomToggleRow(
            isHidden = state.isHidden,
            onToggle = { onEvent(ChallengeGroupSettingsInfoEvent.ToggleHidden(it)) }
        )

        if (state.isHidden) {
            Spacer(modifier = Modifier.height(12.dp))
            GroupFormLabel("비밀번호")
            BaseTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                placeHolderValue = "비밀번호를 입력하세요",
                onTextChanged = { onEvent(ChallengeGroupSettingsInfoEvent.ChangePassword(it)) },
                visualTransformation = PasswordVisualTransformation()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        BaseBottomBtn(
            modifier = Modifier.fillMaxWidth(),
            text = "저장하기",
            enabled = state.isValid,
            onClick = { onEvent(ChallengeGroupSettingsInfoEvent.SaveChanges) }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(widthDp = 400)
@Composable
private fun SettingsInfoContentPreview() {
    ChallengeGroupSettingsInfoContent(
        state = ChallengeGroupSettingsInfoState(
            title = "커피 끊기 챌린지",
            reward = "30000",
            category = "커피",
            hashtags = "#절약 #커피",
            maxSize = "10",
            isHidden = false
        ),
        onEvent = {}
    )
}
