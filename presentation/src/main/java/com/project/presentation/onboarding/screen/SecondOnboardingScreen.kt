package com.project.presentation.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseIcon
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white


@Composable
@Preview(widthDp = 700)
fun SecondOnboardingScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gray700)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                BaseIcon(
                    modifier = Modifier.fillMaxSize(),
                    iconId = R.drawable.img_onboarding2_bg,
                    contentScale = ContentScale.FillHeight
                )

                SecondOnboardingContent(
                    nickname = state.nickname
                )

                BaseBottomBtn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .align(Alignment.BottomCenter),
                    text = stringResource(R.string.common_start),
                    backgroundColor = white,
                    textColor = black,
                    onClick = {
                        navHostController.navigate(NavItem.ThirdOnboarding.route)
                    }
                )
            }
        }
    }
}

@Composable
fun SecondOnboardingContent(
    nickname: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = stringResource(R.string.onboarding_second_title).replace("#VALUE#", nickname),
            style = goolbitgTypography.h1,
            color = white
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.onboarding_second_sub_title),
            style = goolbitgTypography.body1,
            color = gray300
        )
    }
}
