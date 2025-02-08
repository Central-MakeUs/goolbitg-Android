package com.project.presentation.onboarding.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseDatePicker
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseLoadingBox
import com.project.presentation.base.BaseOutlinedTextFiled
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.challenge.addition.calculateScrimAlpha
import com.project.presentation.challenge.addition.getScreenHeightInPixelsOnce
import com.project.presentation.common.AgreementEnum
import com.project.presentation.common.GenderEnum
import com.project.presentation.common.NicknameStatus
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.OnboardingEvent
import com.project.presentation.onboarding.OnboardingState
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.error
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.main15
import com.project.presentation.ui.theme.main20
import com.project.presentation.ui.theme.roundLg
import com.project.presentation.ui.theme.spacingLg
import com.project.presentation.ui.theme.spacingMd
import com.project.presentation.ui.theme.spacingXxl
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(widthDp = 700)
fun FirstOnboardingScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    var nextBtnState by remember {
        mutableStateOf(false)
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    val coroutineScope = rememberCoroutineScope()
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
    var isAgreementBottomSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state) {
        nextBtnState = state.isFirstOnboardingCompleted()
        if (state.isFirstOnboardingSuccess) {
            navHostController.navigate(NavItem.SecondOnboarding.route) {
                popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            BottomSheetScaffold(
                modifier = Modifier
                    .padding(innerPadding)
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
                    if (isAgreementBottomSheet) {
                        AgreementBottomSheetContent(
                            onAgreement = { isAdvertisementAgree ->
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                    isAgreementBottomSheet = false
                                }
                                viewModel.onEvent(OnboardingEvent.RequestFirstOnboarding(isAdvertisementAgree))
                            }
                        )
                    } else {
                        BirthBottomSheetContent(
                            initYear = state.currDate.year.toString(),
                            initMonth = state.currDate.monthValue.toString(),
                            initDay = state.currDate.dayOfMonth.toString(),
                            yearList = (1900..state.currDate.year).map { it.toString() },
                            onConfirm = { year, month, day ->
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                                viewModel.onEvent(event = OnboardingEvent.ChangeYear(year))
                                viewModel.onEvent(event = OnboardingEvent.ChangeMonth(month))
                                viewModel.onEvent(event = OnboardingEvent.ChangeDay(day))
                            }
                        )
                    }

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
                                            isAgreementBottomSheet = false
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        FirstOnboardingBody(
                            modifier = Modifier.fillMaxSize(),
                            state = state,
                            onNicknameChanged = {
                                viewModel.onEvent(
                                    event = OnboardingEvent.ChangeNickname(it)
                                )
                            },
                            onDuplicationCheck = {
                                focusManager.clearFocus(force = true)
                                viewModel.onEvent(event = OnboardingEvent.DuplicationCheck)
                            },
                            onBirthClick = {
                                focusManager.clearFocus(force = true)
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            onMaleClick = {
                                focusManager.clearFocus(force = true)
                                viewModel.onEvent(event = OnboardingEvent.ClickMale)
                            },
                            onFemaleClick = {
                                focusManager.clearFocus(force = true)
                                viewModel.onEvent(event = OnboardingEvent.ClickFemale)
                            }
                        )

                        AnimatedVisibility(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .align(Alignment.BottomCenter),
                            visible = nextBtnState,
                            enter = fadeIn()
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = spacingMd)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                main100,
                                                Color(0xFF67BF4E)
                                            )
                                        )
                                    )
                                    .noRippleClickable {
                                        coroutineScope.launch {
                                            focusManager.clearFocus(force = true)
                                            isAgreementBottomSheet = true
                                            scaffoldState.bottomSheetState.expand()
                                        }
                                    }
                                    .padding(vertical = 16.dp),
                                text = stringResource(R.string.onboarding_first_agreement_start),
                                textAlign = TextAlign.Center,
                                style = goolbitgTypography.btn1,
                                color = white
                            )
                        }
                    }
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
            )
        }

        if (state.isLoading) {
            BaseLoadingBox()
        }
    }

}

@Composable
fun FirstOnboardingBody(
    state: OnboardingState,
    modifier: Modifier = Modifier,
    onNicknameChanged: (String) -> Unit,
    onDuplicationCheck: () -> Unit,
    onBirthClick: () -> Unit,
    onMaleClick: () -> Unit,
    onFemaleClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier.padding(top = 36.dp, start = 24.dp, end = 24.dp)) {
        Text(
            text = stringResource(R.string.onboarding_first_title),
            style = goolbitgTypography.h1,
            color = white
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.onboarding_first_sub_title),
            style = goolbitgTypography.body1,
            color = gray300
        )
        Spacer(modifier = Modifier.height(spacingXxl))
        InputNicknameContent(
            modifier = Modifier.widthIn(max = 400.dp),
            nickname = state.nickname,
            nicknameStatus = state.nicknameStatus,
            onNicknameChanged = onNicknameChanged,
            onDuplicationCheck = onDuplicationCheck
        )
        Spacer(modifier = Modifier.height(spacingLg))
        InputBirthContent(
            modifier = Modifier.widthIn(max = 400.dp),
            year = state.year,
            month = state.month,
            day = state.day,
            onClick = onBirthClick
        )
        Spacer(modifier = Modifier.height(spacingLg))
        SelectGenderContent(
            modifier = Modifier.widthIn(max = 400.dp),
            gender = state.gender,
            onMaleClick = onMaleClick,
            onFemaleClick = onFemaleClick
        )
    }
}

@Composable
fun InputNicknameContent(
    nickname: String,
    nicknameStatus: NicknameStatus,
    onDuplicationCheck: () -> Unit,
    onNicknameChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.onboarding_first_nickname_title),
                style = goolbitgTypography.caption1,
                color = white
            )
            Text(text = " *", style = goolbitgTypography.caption1, color = error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseOutlinedTextFiled(
                modifier = Modifier
                    .widthIn(max = 238.dp),
                value = nickname,
                textStyle = goolbitgTypography.caption2,
                placeholder = {
                    Text(
                        text = stringResource(R.string.onboarding_first_nickname_placeholder),
                        style = goolbitgTypography.caption2,
                        color = gray500
                    )
                },
                shape = RoundedCornerShape(6.dp),
                onValueChange = {
                    if (it != nickname) {
                        onNicknameChanged(it)
                    }
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            if (nicknameStatus == NicknameStatus.NotErr) {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(white)
                        .noRippleClickable { onDuplicationCheck() }
                        .padding(horizontal = 16.dp, vertical = 14.5.dp),
                    text = stringResource(R.string.onboarding_first_nickname_check),
                    style = goolbitgTypography.btn3,
                    color = black
                )
            } else {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFF3E3E3E),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(Color(0xFF212121))
                        .padding(horizontal = 16.dp, vertical = 14.5.dp),
                    text = stringResource(R.string.onboarding_first_nickname_check),
                    style = goolbitgTypography.btn3,
                    color = Color(0xFF3E3E3E)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val color = if (nicknameStatus == NicknameStatus.Completed) Color(0xFF4BB329) else error
        Text(
            text = nicknameStatus.strId?.let { stringResource(it) } ?: "",
            color = color,
            style = goolbitgTypography.caption2
        )

    }
}

@Composable
fun InputBirthContent(
    year: String,
    month: String,
    day: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.onboarding_first_birth_title),
                style = goolbitgTypography.caption1,
                color = white
            )
            Text(text = " *", style = goolbitgTypography.caption1, color = error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        val isBirthEmpty = year.isEmpty() || month.isEmpty() || day.isEmpty()
        val birthStr = if (isBirthEmpty) {
            stringResource(R.string.onboarding_first_birth_placeholder)
        } else {
            "${year}년 ${month.padStart(2, '0')}월 ${day.padStart(2, '0')}일"
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(width = 1.dp, color = gray500, shape = RoundedCornerShape(6.dp))
                .background(gray600)
                .noRippleClickable { onClick() }
                .padding(spacingMd),
            text = birthStr,
            color = if (isBirthEmpty) gray400 else white,
            style = goolbitgTypography.caption2
        )

    }
}

@Composable
fun SelectGenderContent(
    gender: GenderEnum?,
    modifier: Modifier = Modifier,
    onMaleClick: () -> Unit,
    onFemaleClick: () -> Unit
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.onboarding_first_gender_title),
                style = goolbitgTypography.caption1,
                color = white
            )
            Text(text = " *", style = goolbitgTypography.caption1, color = error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
        ) {
            GenderButton(
                isSelected = gender == GenderEnum.Male,
                text = stringResource(R.string.onboarding_first_gender_male),
                modifier = Modifier.weight(1f),
                onClick = onMaleClick
            )

            Spacer(modifier = Modifier.width(16.dp))

            GenderButton(
                isSelected = gender == GenderEnum.Female,
                text = stringResource(R.string.onboarding_first_gender_female),
                modifier = Modifier.weight(1f),
                onClick = onFemaleClick
            )
        }
    }
}

@Composable
fun GenderButton(
    isSelected: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (bg, textColor) = if (isSelected) {
        Pair(white, black)
    } else {
        Pair(gray500, gray200)
    }

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(roundLg))
            .background(bg)
            .noRippleClickable { onClick() }
            .padding(16.dp),
        text = text,
        color = textColor,
        style = goolbitgTypography.btn3,
        textAlign = TextAlign.Center
    )
}

@Composable
fun BirthBottomSheetContent(
    initYear: String,
    initMonth: String,
    initDay: String,
    yearList: List<String>,
    modifier: Modifier = Modifier,
    onConfirm: (String, String, String) -> Unit
) {
    var year by remember { mutableStateOf(initYear) }
    var month by remember { mutableStateOf(initMonth) }
    var day by remember { mutableStateOf(initDay) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 26.dp)
    ) {
        BaseDatePicker(
            yearList = yearList,
            onYearChanged = {
                year = it
            },
            onMonthChanged = {
                month = it
            },
            onDayChanged = {
                day = it
            },
        )
        Spacer(modifier = Modifier.height(42.dp))
        BaseBottomBtn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(R.string.common_apply),
            onClick = { onConfirm(year, month, day) }
        )
        // 이거 안해주면 시스템 네비게이션바랑 겹침
        Spacer(
            modifier =
            Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
        )
    }
}

@Composable
fun AgreementBottomSheetContent(
    modifier: Modifier = Modifier,
    onAgreement: (Boolean) -> Unit
) {
    var isAllChecked by remember { mutableStateOf(false) }
    val agreementList = AgreementEnum.entries
    var agreementCheckList by remember { mutableStateOf(List(agreementList.size) { false }) }
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.onboarding_first_agreement_title),
            style = goolbitgTypography.h3,
            color = gray50,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.onboarding_first_agreement_sub_title),
            style = goolbitgTypography.body5,
            color = gray200,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(gray500)
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = if (isAllChecked) main15 else gray400,
                shape = CircleShape
            )
            .background(if (isAllChecked) main20 else gray600)
            .noRippleClickable {
                val newAgreementCheckList = agreementCheckList.map { !isAllChecked }
                agreementCheckList = newAgreementCheckList
                isAllChecked = !isAllChecked
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseIcon(iconId = if (isAllChecked) R.drawable.ic_checkbox_green else R.drawable.ic_checkbox_gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.onboarding_first_agreement_all),
                color = if (isAllChecked) main100 else white,
                style = goolbitgTypography.body4
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            agreementList.forEachIndexed { idx, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noRippleClickable {
                            val newAgreementCheckList = agreementCheckList
                                .map { it }
                                .toMutableList()
                                .apply {
                                    this[idx] = !this[idx]
                                }
                            agreementCheckList = newAgreementCheckList
                            isAllChecked = newAgreementCheckList.all { it }
                        }
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BaseIcon(iconId = if (agreementCheckList[idx]) R.drawable.ic_checkbox_green else R.drawable.ic_checkbox_gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(item.strId),
                        color = gray300,
                        style = goolbitgTypography.body4
                    )
                    BaseIcon(
                        modifier = Modifier.noRippleClickable {
                            if (item.webUrl != null) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.webUrl))
                                context.startActivity(intent)
                            }
                        },
                        iconId = R.drawable.ic_arrow_right_over
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(gray500)
        )

        val isActive = agreementCheckList.subList(0, agreementCheckList.size - 1).all { it }
        BaseBottomBtn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(R.string.common_start),
            textColor = if (isActive) white else gray400,
            backgroundColor = if (isActive) main100 else gray500,
            onClick = { onAgreement(agreementCheckList[agreementCheckList.size - 1]) }
        )

        // 이거 안해주면 시스템 네비게이션바랑 겹침
        Spacer(
            modifier =
            Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
        )
    }
}
