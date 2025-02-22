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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseGifImage
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

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.illu_onboarding2_card)
    )
    val lottieAnimatable = rememberLottieAnimatable()
    LaunchedEffect(composition) {
        lottieAnimatable.animate(
            composition = composition,
            clipSpec = LottieClipSpec.Frame(0, 1200),
            initialProgress = 0f
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gray700)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                BaseGifImage(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    rawId = R.raw.illu_onboarding2_card
                )

                SecondOnboardingContent(
                    modifier = Modifier.fillMaxSize(),
                    nickname = state.nickname.ifEmpty {
                        state.localNickname
                    }
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
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = stringResource(R.string.onboarding_second_title).replace(
                    "#VALUE#",
                    nickname
                ),
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
        Spacer(modifier = Modifier.height(20.dp))
    }
}
