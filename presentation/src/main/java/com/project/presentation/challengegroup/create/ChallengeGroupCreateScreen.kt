package com.project.presentation.challengegroup.create

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseHeader
import com.project.presentation.base.BaseTextField
import com.project.presentation.base.BaseTwoButtonPopup
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.challengegroup.create.ChallengeGroupCreateState.Companion.CATEGORY_OPTIONS
import com.project.presentation.challengegroup.create.ChallengeGroupCreateState.Companion.MAX_HASHTAGS
import com.project.presentation.challengegroup.create.ChallengeGroupCreateState.Companion.MAX_PARTICIPANTS
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.error
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
fun ChallengeGroupCreateScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeGroupCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { navHostController.popBackStack() }
    }

    val tryBack: () -> Unit = {
        if (state.isModified) showExitDialog = true
        else navHostController.popBackStack()
    }
    BackHandler(enabled = state.isModified) { showExitDialog = true }

    Scaffold(
        containerColor = bg1,
        topBar = {
            BaseHeader(title = "작심삼일 방 생성하기", onBackPressed = tryBack)
        }
    ) { innerPadding ->
        ChallengeGroupCreateContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showExitDialog) {
        BaseTwoButtonPopup(
            title = "작심삼일 생성 중단",
            subTitle = "작심삼일 생성하기를\n정말 중단하시겠어요?",
            confirmText = "중단",
            dismissText = "취소",
            confirmBgColor = error,
            confirmTextColor = white,
            dismissBgColor = gray400,
            dismissTextColor = gray100,
            onConfirm = {
                showExitDialog = false
                navHostController.popBackStack()
            },
            onDismiss = { showExitDialog = false }
        )
    }
}

// ─── Stateless ───────────────────────────────────────────────────────────────
@Composable
fun ChallengeGroupCreateContent(
    state: ChallengeGroupCreateState,
    onEvent: (ChallengeGroupCreateEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 1) 스크롤 폼
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 챌린지명
            GroupFormLabel("챌린지명 *")
            BaseTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                placeHolderValue = "챌린지명을 입력하세요",
                onTextChanged = { onEvent(ChallengeGroupCreateEvent.ChangeTitle(it)) }
            )

            // 챌린지 금액 — ₩ 접두사 + 천 단위 콤마
            Spacer(modifier = Modifier.height(16.dp))
            GroupFormLabel("챌린지 금액 *")
            BaseTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.reward,
                placeHolderValue = "₩ 0",
                onTextChanged = { onEvent(ChallengeGroupCreateEvent.ChangeReward(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = WonCurrencyVisualTransformation()
            )
            state.rewardError?.let { msg ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = msg, style = goolbitgTypography.body5, color = error)
            }

            // 카테고리 — inline 드롭다운
            Spacer(modifier = Modifier.height(16.dp))
            GroupFormLabel("카테고리 *")
            CategorySelector(
                selected = state.category,
                expanded = state.isCategorySheetOpen,
                onClick = {
                    if (state.isCategorySheetOpen) onEvent(ChallengeGroupCreateEvent.CloseCategorySheet)
                    else onEvent(ChallengeGroupCreateEvent.OpenCategorySheet)
                }
            )
            if (state.isCategorySheetOpen) {
                Spacer(modifier = Modifier.height(8.dp))
                CategoryDropdownList(
                    selected = state.category,
                    onSelect = { onEvent(ChallengeGroupCreateEvent.SelectCategory(it)) }
                )
            }

            // 해시태그
            Spacer(modifier = Modifier.height(16.dp))
            GroupFormLabel("해시태그")
            HashtagInputRow(
                input = state.hashtagInput,
                canAdd = state.canAddHashtag,
                onChange = { onEvent(ChallengeGroupCreateEvent.ChangeHashtagInput(it)) },
                onAdd = { onEvent(ChallengeGroupCreateEvent.AddHashtag) }
            )
            if (state.hashtags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HashtagChips(
                    tags = state.hashtags,
                    onRemove = { onEvent(ChallengeGroupCreateEvent.RemoveHashtag(it)) }
                )
            }

            // 최대 인원 — 라벨 + "최대 10명" 안내 + 카운터를 한 줄로
            Spacer(modifier = Modifier.height(16.dp))
            MaxSizeRow(
                value = state.maxSize,
                canDecrease = state.canDecreaseMaxSize,
                canIncrease = state.canIncreaseMaxSize,
                onDecrease = { onEvent(ChallengeGroupCreateEvent.DecreaseMaxSize) },
                onIncrease = { onEvent(ChallengeGroupCreateEvent.IncreaseMaxSize) }
            )

            // 비밀방
            Spacer(modifier = Modifier.height(16.dp))
            HiddenRoomToggleRow(
                isHidden = state.isHidden,
                onToggle = { onEvent(ChallengeGroupCreateEvent.ToggleHidden(it)) }
            )
            if (state.isHidden) {
                Spacer(modifier = Modifier.height(12.dp))
                GroupFormLabel("비밀방 비밀번호 *")
                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    placeHolderValue = "비밀번호를 입력하세요",
                    onTextChanged = { onEvent(ChallengeGroupCreateEvent.ChangePassword(it)) },
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            // 하단 버튼 영역 buffer
            Spacer(modifier = Modifier.height(96.dp))
        }

        // 2) 조건부 하단 버튼 — fadeIn
        BaseBottomBtn(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            text = "생성하기",
            visible = state.isValid,
            onClick = { onEvent(ChallengeGroupCreateEvent.Submit) }
        )
    }
}

// ─── Sub-components ──────────────────────────────────────────────────────────
@Composable
fun GroupFormLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = goolbitgTypography.body5,
        color = gray300,
        modifier = modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun CategorySelector(
    selected: String?,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(gray700)
            .border(1.dp, gray500, RoundedCornerShape(12.dp))
            .noRippleClickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = selected ?: "카테고리 선택하기",
            style = goolbitgTypography.body4,
            color = if (selected != null) white else gray400
        )
        Text(
            text = if (expanded) "▴" else "▾",
            style = goolbitgTypography.body3,
            color = gray300
        )
    }
}

@Composable
private fun CategoryDropdownList(
    selected: String?,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(gray700)
            .border(1.dp, gray500, RoundedCornerShape(12.dp))
    ) {
        CATEGORY_OPTIONS.forEachIndexed { idx, option ->
            val isSelected = option == selected
            if (idx > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(gray600)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { onSelect(option) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = option,
                    style = goolbitgTypography.body4,
                    color = if (isSelected) main100 else white
                )
                if (isSelected) {
                    Text(text = "✓", style = goolbitgTypography.body3, color = main100)
                }
            }
        }
    }
}

@Composable
private fun HashtagInputRow(
    input: String,
    canAdd: Boolean,
    onChange: (String) -> Unit,
    onAdd: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BaseTextField(
            modifier = Modifier.weight(1f),
            value = input,
            placeHolderValue = "해시태그를 추가하세요",
            onTextChanged = onChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        AddHashtagButton(enabled = canAdd, onClick = onAdd)
    }
}

@Composable
private fun AddHashtagButton(enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) gray500 else gray600
    val textColor = if (enabled) gray100 else gray400
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .let { if (enabled) it.noRippleClickable { onClick() } else it }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "+추가",
            style = goolbitgTypography.btn4,
            color = textColor
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HashtagChips(
    tags: List<String>,
    onRemove: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(gray700)
                    .border(1.dp, gray500, RoundedCornerShape(100.dp))
                    .padding(start = 12.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#$tag",
                    style = goolbitgTypography.body5,
                    color = gray200
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .noRippleClickable { onRemove(tag) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "×", style = goolbitgTypography.body4, color = gray300)
                }
            }
        }
    }
}

@Composable
private fun MaxSizeRow(
    value: Int,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "최대 인원 설정 *",
            style = goolbitgTypography.body5,
            color = gray300
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = "최대 ${MAX_PARTICIPANTS}명",
            style = goolbitgTypography.body5,
            color = gray400
        )
        InlineCounterButton(symbol = "−", enabled = canDecrease, onClick = onDecrease)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$value",
            style = goolbitgTypography.body3,
            color = white,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(8.dp))
        InlineCounterButton(symbol = "+", enabled = canIncrease, onClick = onIncrease)
    }
}

@Composable
private fun InlineCounterButton(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) gray500 else gray600
    val textColor = if (enabled) gray100 else gray400
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .let { if (enabled) it.noRippleClickable { onClick() } else it },
        contentAlignment = Alignment.Center
    ) {
        Text(text = symbol, style = goolbitgTypography.body3, color = textColor)
    }
}

@Composable
fun HiddenRoomToggleRow(
    isHidden: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "비밀방",
            style = goolbitgTypography.body5,
            color = gray300
        )
        OnOffToggle(checked = isHidden, onChange = onToggle)
    }
}

@Composable
private fun OnOffToggle(
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val bg = if (checked) main100 else gray500
    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 31.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(bg)
            .noRippleClickable { onChange(!checked) }
    ) {
        // 라벨 — ON 이면 좌측, OFF 이면 우측
        Text(
            text = if (checked) "ON" else "OFF",
            style = goolbitgTypography.body5,
            color = white,
            modifier = Modifier
                .align(if (checked) Alignment.CenterStart else Alignment.CenterEnd)
                .padding(horizontal = 10.dp)
        )
        // knob — ON 이면 우측, OFF 이면 좌측
        Box(
            modifier = Modifier
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(horizontal = 2.dp)
                .size(27.dp)
                .clip(CircleShape)
                .background(white)
        )
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────
/** "20000" → "₩ 20,000" 으로 보여주는 visual transformation. raw 입력은 숫자만. */
private class WonCurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val number = raw.toLongOrNull()
        val display = if (number == null) raw else "₩ %,d".format(number)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = display.length
            override fun transformedToOriginal(offset: Int): Int = raw.length
        }
        return TransformedText(AnnotatedString(display), mapping)
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(widthDp = 400, heightDp = 800)
@Composable
private fun CreateContentFilledPreview() {
    ChallengeGroupCreateContent(
        state = ChallengeGroupCreateState(
            title = "택시안타고 막차타기",
            reward = "20000",
            category = "교통",
            hashtagInput = "막차챌린지",
            hashtags = listOf("직장인", "역삼동"),
            maxSize = 4,
            isHidden = true,
            password = "0428"
        ),
        onEvent = {}
    )
}

@Preview(widthDp = 400, heightDp = 800)
@Composable
private fun CreateContentEmptyPreview() {
    ChallengeGroupCreateContent(
        state = ChallengeGroupCreateState(),
        onEvent = {}
    )
}
