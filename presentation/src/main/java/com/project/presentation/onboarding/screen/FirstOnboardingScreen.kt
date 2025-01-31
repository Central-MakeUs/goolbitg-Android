package com.project.presentation.onboarding.screen

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseDatePicker
import com.project.presentation.base.BaseOutlinedTextFiled
import com.project.presentation.common.GenderEnum
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.OnboardingEvent
import com.project.presentation.onboarding.OnboardingState
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import com.project.presentation.ui.theme.error
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.roundLg
import com.project.presentation.ui.theme.spacingLg
import com.project.presentation.ui.theme.spacingMd
import com.project.presentation.ui.theme.spacingXxl

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

    LaunchedEffect(state) {
        nextBtnState = state.isFirstOnboardingCompleted()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                FirstOnboardingBody(
                    modifier = Modifier.fillMaxWidth(),
                    state = state,
                    onNicknameChanged = {
                        viewModel.onEvent(
                            event = OnboardingEvent.ChangeNickname(
                                it
                            )
                        )
                    },
                    onYearChanged = { viewModel.onEvent(event = OnboardingEvent.ChangeYear(it)) },
                    onMonthChanged = { viewModel.onEvent(event = OnboardingEvent.ChangeMonth(it)) },
                    onDayChanged = { viewModel.onEvent(event = OnboardingEvent.ChangeDay(it)) },
                    onMaleClick = { viewModel.onEvent(event = OnboardingEvent.ClickMale) },
                    onFemaleClick = { viewModel.onEvent(event = OnboardingEvent.ClickFemale) }
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
                                brush = Brush.horizontalGradient(listOf(main100, Color(0xFF67BF4E)))
                            )
                            .clickable {
                                navHostController.navigate(NavItem.SecondOnboarding.route)
                            }
                            .padding(vertical = 16.dp),
                        text = stringResource(R.string.common_start),
                        textAlign = TextAlign.Center,
                        style = goolbitgTypography.btn1,
                        color = white
                    )
                }
            }
        }
    }

}

@Composable
fun FirstOnboardingBody(
    state: OnboardingState,
    modifier: Modifier = Modifier,
    onNicknameChanged: (String) -> Unit,
    onYearChanged: (String) -> Unit,
    onMonthChanged: (String) -> Unit,
    onDayChanged: (String) -> Unit,
    onMaleClick: () -> Unit,
    onFemaleClick: () -> Unit
) {
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
            onNicknameChanged = onNicknameChanged
        )
        Spacer(modifier = Modifier.height(spacingLg))
        InputBirthContent(
            modifier = Modifier.widthIn(max = 400.dp),
            year = state.year,
            month = state.month,
            day = state.day,
            onYearChanged = onYearChanged,
            onMonthChanged = onMonthChanged,
            onDayChanged = onDayChanged
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
                onValueChange = onNicknameChanged
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(white)
                    .padding(horizontal = 16.dp, vertical = 14.5.dp),
                text = stringResource(R.string.onboarding_first_nickname_check),
                style = goolbitgTypography.btn3,
                color = black
            )
        }

    }
}

@Composable
fun InputBirthContent(
    year: String,
    month: String,
    day: String,
    modifier: Modifier = Modifier,
    onYearChanged: (String) -> Unit,
    onMonthChanged: (String) -> Unit,
    onDayChanged: (String) -> Unit
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

        Row {
//            BaseDatePicker(
//                yearList = (1900..2025).map { it.toString() },
//                onYearChanged = onYearChanged,
//                onMonthChanged = onMonthChanged,
//                onDayChanged = onDayChanged,
//            )

        }
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
            .clickable { onClick() }
            .padding(16.dp),
        text = text,
        color = textColor,
        style = goolbitgTypography.btn3,
        textAlign = TextAlign.Center
    )
}