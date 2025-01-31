package com.project.presentation.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseFormDropdown
import com.project.presentation.base.BaseTimePicker
import com.project.presentation.common.DayOfWeekEnum
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.OnboardingEvent
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@Composable
@Preview(showBackground = true)
fun FifthOnboardingScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val isNextStepVisible by remember(state.value) {
        derivedStateOf {
            state.value.majorExpenditureDayOfWeek != null
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(
            containerColor = transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                FifthOnboardingContent(
                    selectedDayOfWeek = state.value.majorExpenditureDayOfWeek,
                    isTimeVisible = isNextStepVisible,
                    onDayOfWeekSelected = {
                        viewModel.onEvent(OnboardingEvent.SelectMajorExpenditureDayOfWeek(it))
                    },
                    onHourChanged = {
                        viewModel.onEvent(OnboardingEvent.ChangeMajorExpenditureHour(it))
                    },
                    onMinuteChanged = {
                        viewModel.onEvent(OnboardingEvent.ChangeMajorExpenditureMinute(it))
                    },
                    onAmpmChanged = {
                        viewModel.onEvent(OnboardingEvent.ChangeMajorExpenditureAmpm(it))
                    }
                )
                BaseBottomBtn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    text = stringResource(R.string.common_next),
                    visible = isNextStepVisible,
                    onClick = {
                        navHostController.navigate(NavItem.AnalysisConsumeType.route)
                    },
                )
            }
        }
    }
}

@Composable
fun FifthOnboardingContent(
    selectedDayOfWeek: DayOfWeekEnum?,
    isTimeVisible: Boolean,
    modifier: Modifier = Modifier,
    onDayOfWeekSelected: (DayOfWeekEnum) -> Unit,
    onHourChanged: (String) -> Unit,
    onMinuteChanged: (String) -> Unit,
    onAmpmChanged: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 20.dp),
                text = stringResource(R.string.onboarding_fifth_title),
                style = goolbitgTypography.h1,
                color = white
            )

            Text(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                text = stringResource(R.string.common_skip),
                style = goolbitgTypography.h3,
                color = gray300
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.onboarding_fifth_sub_title),
            color = gray300
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            if (isTimeVisible) {
                TimePickerContent(
                    onHourChanged = onHourChanged,
                    onMinuteChanged = onMinuteChanged,
                    onAmpmChanged = onAmpmChanged
                )
            }

            val dayOfWeekList = DayOfWeekEnum.entries
            BaseFormDropdown(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.onboarding_fifth_major_expenditure_day),
                hint = stringResource(R.string.onboarding_fifth_major_expenditure_day_placeholder),
                selectedItem = selectedDayOfWeek?.let {
                    stringResource(it.strId)
                },
                itemList = dayOfWeekList.map { stringResource(it.strId) },
                onItemSelected = { idx ->
                    onDayOfWeekSelected(dayOfWeekList[idx])
                }
            )
        }

    }
}

@Composable
fun TimePickerContent(
    modifier: Modifier = Modifier,
    onHourChanged: (String) -> Unit,
    onMinuteChanged: (String) -> Unit,
    onAmpmChanged: (String) -> Unit
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(112.dp))
        Text(
            text = stringResource(R.string.onboarding_fifth_major_expenditure_time),
            color = white,
            style = goolbitgTypography.caption1
        )
        Spacer(modifier = Modifier.height(26.dp))
        BaseTimePicker(
            onHourChanged = onHourChanged,
            onMinuteChanged = onMinuteChanged,
            onAmpmChanged = onAmpmChanged
        )
    }
}