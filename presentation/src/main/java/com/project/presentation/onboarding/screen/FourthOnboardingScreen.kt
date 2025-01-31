package com.project.presentation.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTextField
import com.project.presentation.base.transformation.Transformation
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.OnboardingEvent
import com.project.presentation.onboarding.OnboardingState
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@Composable
@Preview(showBackground = true)
fun FourthOnboardingScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val isNextBtnVisible by remember(state.value) {
        derivedStateOf {
            state.value.isFourthOnboardingCompleted()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                FourthOnboardingContent(
                    state = state,
                    onAvgIncomeChanged = {
                        viewModel.onEvent(OnboardingEvent.ChangeMonthAvgIncome(it))
                    },
                    onAvgSavingChanged = {
                        viewModel.onEvent(OnboardingEvent.ChangeMonthAvgSaving(it))
                    }
                )

                BaseBottomBtn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    text = stringResource(R.string.common_next),
                    visible = isNextBtnVisible,
                    onClick = {
                        navHostController.navigate(NavItem.FifthOnboarding.route)
                    },
                )
            }
        }
    }
}

@Composable
fun FourthOnboardingContent(
    state: State<OnboardingState>,
    modifier: Modifier = Modifier,
    onAvgIncomeChanged: (String) -> Unit,
    onAvgSavingChanged: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {

        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            FourthOnboardingTitleContent()

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.onboarding_fourth_sub_title),
                style = goolbitgTypography.body1,
                color = gray300
            )
            Spacer(modifier = Modifier.height(40.dp))

            MonthAvgTextFiledLabel(
                text = stringResource(R.string.onboarding_fourth_month_avg_income),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            BaseTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.value.monthAvgIncome,
                prefixValue = "₩ ",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                placeHolderValue = stringResource(R.string.onboarding_fourth_month_avg_income_placeholder),
                onTextChanged = onAvgIncomeChanged,
                visualTransformation = Transformation.thousandSeparatorTransformation()
            )

            Spacer(modifier = Modifier.height(40.dp))

            MonthAvgTextFiledLabel(
                text = stringResource(R.string.onboarding_fourth_month_avg_saving),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            BaseTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.value.monthAvgSaving,
                prefixValue = "₩ ",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                placeHolderValue = stringResource(R.string.onboarding_fourth_month_avg_saving_placeholder),
                onTextChanged = onAvgSavingChanged,
                visualTransformation = Transformation.thousandSeparatorTransformation()
            )
        }
    }
}


@Composable
fun FourthOnboardingTitleContent(
    modifier: Modifier = Modifier,
) {
    class CustomPopupPositionProvider: PopupPositionProvider{
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowSize: IntSize,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize
        ): IntOffset {
            return IntOffset(
                x = (anchorBounds.left + anchorBounds.size.width / 2),
                y = (anchorBounds.bottom) + 8 // 8dp 여백
            )
        }

    }

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(38.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.onboarding_fourth_title),
                style = goolbitgTypography.h1,
                color = white
            )
            Spacer(modifier = Modifier.width(8.dp))
            BaseIcon(iconId = R.drawable.ic_tooltip_marker)

        }
    }
}

@Composable
fun MonthAvgTextFiledLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = goolbitgTypography.caption1,
            color = white
        )
        Text(
            text = " *",
            style = goolbitgTypography.caption1,
            color = com.project.presentation.ui.theme.error
        )
    }
}