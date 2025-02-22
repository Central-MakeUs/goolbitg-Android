package com.project.presentation.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
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
import com.project.presentation.ui.theme.roundLg
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

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
            Box(modifier = Modifier.fillMaxSize()) {
                ShowConsumeTypeContent(
                    state = state,
                    modifier = Modifier.padding(innerPadding),
                    onNext = {
                        val route =
                            NavItem.ChallengeAddition.route.replace("{isOnboarding}", "true")
                        navHostController.navigate(route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
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
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        val nickname = state.value.nickname.ifEmpty {
            state.value.localNickname
        }
        val title = stringResource(R.string.show_consume_type_title)
            .replace("#NICKNAME#", nickname)
            .replace("#TYPE#", state.value.userInfoModel?.spendingType?.title ?: "")
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = title,
            style = goolbitgTypography.h1,
            color = white
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = stringResource(R.string.show_consume_type_sub_title),
            style = goolbitgTypography.body1,
            color = gray300
        )

        Spacer(modifier = Modifier.height(14.dp))

        ConsumeTypeCard(
            modifier = Modifier.weight(1f),
            subTypeName = state.value.userInfoModel?.spendingType?.let {
                stringResource(R.string.show_consume_depth).replace("#VALUE#", it.id.toString())
            } ?: "",
            typeName = state.value.userInfoModel?.spendingType?.title ?: "",
            imgUrl = state.value.userInfoModel?.spendingType?.imgUrl,
            myConsumeScore = state.value.userInfoModel?.spendingHabitScore ?: 0,
            sameTypeCount = state.value.userInfoModel?.spendingType?.peopleCount ?: 0,
        )

        BaseBottomBtn(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = stringResource(R.string.show_consume_type_btn_text),
            onClick = { onNext() }
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ConsumeTypeCard(
    subTypeName: String,
    typeName: String,
    imgUrl: String?,
    myConsumeScore: Int,
    sameTypeCount: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        val hazeState = remember { HazeState() }

        BaseIcon(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 40.dp)
                .aspectRatio(336f/432f),
            iconId = R.drawable.img_consume_type_card
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .aspectRatio(310f / 393f)
                .background(transparent)
                .align(Alignment.Center)
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = subTypeName, style = goolbitgTypography.body3, color = white)
            Text(text = typeName, style = goolbitgTypography.h1, color = white)
            Spacer(modifier = Modifier.height(30.dp))
            if (imgUrl.isNullOrEmpty()) {
                BaseIcon(modifier = Modifier.size(160.dp), iconId = R.drawable.ic_card_logo)
            } else {
                GlideImage(
                    modifier = Modifier.size(160.dp),
                    model = imgUrl,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
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

            Spacer(modifier = Modifier.height(38.dp))

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
                .background(gray300.copy(alpha = 0.4f))
                .hazeSource(hazeState)
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle.Unspecified.copy(blurRadius = 40.dp)
                )
                .padding(24.dp)
                .align(Alignment.Center)
        )
    }
}
