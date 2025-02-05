package com.project.presentation.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.innerShadow
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.OnboardingState
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.roundLg
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Preview(widthDp = 600)
fun ShowConsumeTypeScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            ShowConsumeTypeContent(
                state = state,
                modifier = Modifier.padding(innerPadding),
                onNext = {
                    val route = NavItem.ChallengeAddition.route.replace("{isOnboarding}", "true")
                    navHostController.navigate(route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun ShowConsumeTypeContent(
    state: State<OnboardingState>,
    modifier: Modifier = Modifier,
    onNext: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        val title = stringResource(R.string.show_consume_type_title).replace(
            "#NICKNAME#",
            state.value.nickname
        ).replace("#TYPE#", state.value.consumeType)
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = goolbitgTypography.h1,
            color = white
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.show_consume_type_sub_title),
            style = goolbitgTypography.body1,
            color = gray300
        )

        Spacer(modifier = Modifier.height(40.dp))

        ConsumeTypeCard(
            modifier = Modifier.weight(1f),
            subTypeName = state.value.consumeTypeSub,
            typeName = state.value.consumeType,
            myConsumeScore = state.value.myConsumeScore,
            sameTypeCount = state.value.sameTypeCount,
        )

        BaseBottomBtn(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(R.string.show_consume_type_btn_text),
            onClick = { onNext() }
        )
    }
}

@Composable
fun ConsumeTypeCard(
    subTypeName: String,
    typeName: String,
    myConsumeScore: Int,
    sameTypeCount: Int,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.illu_show_comsume_type)
    )
    val lottieAnimatable = rememberLottieAnimatable()
    LaunchedEffect(composition) {
        lottieAnimatable.animate(
            composition = composition,
            clipSpec = LottieClipSpec.Frame(0, 1200),
            initialProgress = 0f
        )
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(310.dp, 393.dp)
                .background(transparent)
                .padding(24.dp)
                .align(Alignment.Center)
        ) {
            Text(text = subTypeName, style = goolbitgTypography.h4, color = white)
            Text(text = typeName, style = goolbitgTypography.h1, color = white)
            Spacer(modifier = Modifier.height(16.dp))
            BaseIcon(iconId = R.drawable.ic_card_logo)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.show_consume_type_my_consume_score),
                        style = goolbitgTypography.body4,
                        color = white
                    )
                    Text(
                        text = stringResource(R.string.common_score_value).replace(
                            "#VALUE#",
                            myConsumeScore.toString()
                        ),
                        style = goolbitgTypography.h3,
                        color = white
                    )
                }

                Box(
                    modifier = Modifier
                        .size(1.dp, 40.dp)
                        .background(white.copy(alpha = 0.5f))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.show_consume_type_same_type),
                        style = goolbitgTypography.body4,
                        color = white
                    )
                    Text(
                        text = stringResource(R.string.common_person_count_value).replace(
                            "#VALUE#",
                            sameTypeCount.toString()
                        ),
                        style = goolbitgTypography.h3,
                        color = white
                    )
                }
            }

            Box(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(white)
                    .padding(20.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.show_consume_type_share),
                style = goolbitgTypography.h4,
                color = black
            )
        }

        Box(
            modifier = Modifier
                .size(310.dp, 393.dp)
                .clip(RoundedCornerShape(roundLg))
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(roundLg),
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0x1A71B958), Color(0x4D71B958)
                        )
                    )
                )
                .innerShadow(
                    shape = RoundedCornerShape(roundLg),
                    color = white.copy(alpha = 0.4f),
                    offsetX = 4.dp,
                    offsetY = 4.dp,
                    blur = 4.dp,
                    spread = 0.dp
                )
                .innerShadow(
                    shape = RoundedCornerShape(roundLg),
                    color = black.copy(alpha = 0.4f),
                    offsetX = (-4).dp,
                    offsetY = (-4).dp,
                    blur = 4.dp,
                    spread = 0.dp
                )
                .background(white.copy(alpha = 0.1f))
                .blur(radiusX = 40.dp, radiusY = 40.dp)
                .padding(24.dp)
                .align(Alignment.Center)
        ) {
            BaseIcon(iconId = R.drawable.ic_splash)

        }
        LottieAnimation(
            composition = composition
        )
    }
}