package com.project.presentation.challengegroup.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.domain.model.challengegroup.ChallengeGroupModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTextField
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.challengegroup.main.ChallengeGroupItem
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
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
fun ChallengeGroupSearchScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeGroupSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToDetail.collect { groupId ->
            navHostController.navigate(
                NavItem.ChallengeGroupDetail.route.replace("{groupId}", "$groupId")
            )
        }
    }

    Scaffold(
        containerColor = bg1
    ) { innerPadding ->
        ChallengeGroupSearchContent(
            state = state,
            onEvent = viewModel::onEvent,
            onBack = { navHostController.popBackStack() },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// ─── Stateless ───────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupSearchContent(
    state: ChallengeGroupSearchState,
    onEvent: (ChallengeGroupSearchEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // 헤더 — ← + 검색 pill (statusBars 직접 처리)
        SearchTopBar(
            query = state.query,
            onQueryChange = { onEvent(ChallengeGroupSearchEvent.ChangeQuery(it)) },
            onSearch = { onEvent(ChallengeGroupSearchEvent.Search) },
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("검색 중...", style = goolbitgTypography.body2, color = gray400)
            }
            state.results.isEmpty() && state.query.isNotBlank() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("검색 결과가 없어요", style = goolbitgTypography.body2, color = gray400)
            }
            state.results.isNotEmpty() -> {
                // "검색 결과 총 N건" 라인
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "검색 결과",
                        style = goolbitgTypography.h4,
                        color = white,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "총 ${state.results.size}건",
                        style = goolbitgTypography.body5,
                        color = gray300
                    )
                }
                // 결과 리스트 — 그룹 메인의 ChallengeGroupItem 재사용
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = state.results, key = { it.id }) { group ->
                        ChallengeGroupItem(
                            group = group,
                            onClick = { onEvent(ChallengeGroupSearchEvent.SelectGroup(group)) }
                        )
                    }
                }
            }
        }
    }

    // 통합 참여 다이얼로그 — 공개방/비밀방 모두 같은 다이얼로그 사용
    if (state.showJoinDialog && state.selectedGroup != null) {
        // 다이얼로그 열려 있을 때 시스템 백 버튼으로 닫기
        BackHandler(enabled = true) {
            onEvent(ChallengeGroupSearchEvent.DismissDialog)
        }
        ChallengeGroupJoinDialog(
            group = state.selectedGroup,
            passwordInput = state.passwordInput,
            onPasswordChange = { onEvent(ChallengeGroupSearchEvent.ChangePassword(it)) },
            onConfirm = { onEvent(ChallengeGroupSearchEvent.ConfirmJoin) },
            onDismiss = { onEvent(ChallengeGroupSearchEvent.DismissDialog) }
        )
    }
}

// ─── Sub-components ──────────────────────────────────────────────────────────
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    // 키보드 검색 액션과 우측 돋보기 클릭 모두 동일한 동작:
    // 키보드를 내리고 포커스를 해제한 뒤 검색을 트리거
    val performSearch: () -> Unit = {
        keyboardController?.hide()
        focusManager.clearFocus()
        onSearch()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseIcon(
            modifier = Modifier
                .size(32.dp)
                .noRippleClickable { onBack() },
            iconId = R.drawable.ic_arrow_left
        )
        // 검색 input pill — BaseTextField 의 box 와 중첩되지 않도록 BasicTextField 로 직접 구성
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .background(gray700)
                .border(1.dp, gray500, CircleShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "챌린지명, 해시태그를 검색해보세요",
                        style = goolbitgTypography.body4,
                        color = gray300
                    )
                }
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = goolbitgTypography.body4.copy(color = white),
                    cursorBrush = SolidColor(white),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { performSearch() })
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            BaseIcon(
                modifier = Modifier
                    .size(20.dp)
                    .noRippleClickable { performSearch() },
                iconId = R.drawable.ic_search
            )
        }
    }
}

/**
 * 공개방·비밀방 통합 참여 다이얼로그.
 * 비밀방인 경우 비밀번호 입력 필드를 추가로 노출.
 */
@Composable
fun ChallengeGroupJoinDialog(
    group: ChallengeGroupModel,
    passwordInput: String,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(black.copy(alpha = 0.5f))
            // dim 영역 클릭으로는 닫히지 않도록 클릭 흡수만 함
            .noRippleClickable { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.78f)
                .clip(RoundedCornerShape(16.dp))
                .background(gray600)
                .border(1.dp, gray500, RoundedCornerShape(16.dp))
                .noRippleClickable { /* 다이얼로그 내부 클릭 흡수 */ }
                .padding(20.dp)
        ) {
            // 제목 + 비밀방 뱃지
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = group.title,
                    style = goolbitgTypography.h3,
                    color = white,
                    fontWeight = FontWeight.Bold
                )
                if (group.isHidden) {
                    Spacer(modifier = Modifier.width(8.dp))
                    HiddenBadge()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // 카테고리 + 해시태그 chips
            DialogChipsRow(
                category = group.category,
                hashtags = group.hashtags
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 인원
            Row(verticalAlignment = Alignment.CenterVertically) {
                BaseIcon(modifier = Modifier.size(14.dp), iconId = R.drawable.ic_users_color)
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${group.peopleCount}/${group.maxSize} 참여중",
                    style = goolbitgTypography.body5,
                    color = main100
                )
            }

            // 비밀번호 입력 (비밀방만)
            if (group.isHidden) {
                Spacer(modifier = Modifier.height(16.dp))
                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = passwordInput,
                    placeHolderValue = "숫자 4자리를 입력해주세요",
                    onTextChanged = { input ->
                        // 숫자만, 4자리로 제한
                        onPasswordChange(input.filter { it.isDigit() }.take(4))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            // 취소 / 참여 버튼
            Row(modifier = Modifier.fillMaxWidth()) {
                DialogButton(
                    modifier = Modifier.weight(1f),
                    text = "취소",
                    backgroundColor = gray400,
                    textColor = gray100,
                    onClick = onDismiss
                )
                Spacer(modifier = Modifier.width(12.dp))
                DialogButton(
                    modifier = Modifier.weight(1f),
                    text = "참여",
                    backgroundColor = main100,
                    textColor = white,
                    onClick = onConfirm
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DialogChipsRow(category: String, hashtags: List<String>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (category.isNotBlank()) {
            // 카테고리 — filled chip (강조)
            DialogCategoryChip(text = category)
        }
        hashtags.forEach { tag ->
            // 해시태그 — outline chip (보조)
            DialogHashtagChip(text = "#$tag")
        }
    }
}

@Composable
private fun DialogCategoryChip(text: String) {
    // 카테고리 — filled chip (다이얼로그 배경 gray600 보다 살짝 밝은 fill)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(gray500)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = text, style = goolbitgTypography.body5, color = gray100)
    }
}

@Composable
private fun DialogHashtagChip(text: String) {
    // 해시태그 — outline chip (border 만, fill 없음)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, gray500, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = text, style = goolbitgTypography.body5, color = gray200)
    }
}

@Composable
private fun HiddenBadge() {
    // 비밀방 뱃지 — 다이얼로그(gray600) 위에 살짝 밝은 fill 로 떠 보이도록 gray500
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(gray500)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        BaseIcon(modifier = Modifier.size(12.dp), iconId = R.drawable.ic_lock)
        Text(text = "비밀방", style = goolbitgTypography.body5, color = gray100)
    }
}

@Composable
private fun DialogButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .noRippleClickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = goolbitgTypography.btn2,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(widthDp = 400, heightDp = 800)
@Composable
private fun SearchContentPreview() {
    ChallengeGroupSearchContent(
        state = ChallengeGroupSearchState(
            query = "커피",
            results = listOf(
                ChallengeGroupModel(
                    id = 1, ownerId = "user1", title = "거지방챌린지", reward = 30000,
                    hashtags = listOf("배달줄일까말까줄일까말까줄일까말까"), category = "식비", maxSize = 6,
                    peopleCount = 3, isHidden = true, password = null,
                    avgAchieveRatio = 0.8f, maxAchieveDays = 3
                ),
                ChallengeGroupModel(
                    id = 2, ownerId = "user2", title = "택시말고 막차타기", reward = 20000,
                    hashtags = listOf("배달줄이기", "배민", "야식"), category = "교통비", maxSize = 6,
                    peopleCount = 4, isHidden = false, password = null,
                    avgAchieveRatio = 0.5f, maxAchieveDays = 2
                )
            )
        ),
        onEvent = {},
        onBack = {}
    )
}

@Preview(widthDp = 400, heightDp = 800)
@Composable
private fun JoinDialogHiddenPreview() {
    ChallengeGroupJoinDialog(
        group = ChallengeGroupModel(
            id = 1, ownerId = "user1", title = "커피 값 모아 태산", reward = 30000,
            hashtags = listOf("배민", "야식"), category = "식비", maxSize = 10,
            peopleCount = 2, isHidden = true, password = null,
            avgAchieveRatio = 0.5f, maxAchieveDays = 1
        ),
        passwordInput = "",
        onPasswordChange = {},
        onConfirm = {},
        onDismiss = {}
    )
}

@Preview(widthDp = 400, heightDp = 800)
@Composable
private fun JoinDialogOpenPreview() {
    ChallengeGroupJoinDialog(
        group = ChallengeGroupModel(
            id = 1, ownerId = "user1", title = "커피 값 모아 태산", reward = 30000,
            hashtags = listOf("배민", "야식"), category = "식비", maxSize = 10,
            peopleCount = 2, isHidden = false, password = null,
            avgAchieveRatio = 0.5f, maxAchieveDays = 1
        ),
        passwordInput = "",
        onPasswordChange = {},
        onConfirm = {},
        onDismiss = {}
    )
}
