package com.project.presentation.login

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.domain.model.user.RegisterStatus
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.transparent

@Composable
@Preview
fun LoginScreen(
    navHostController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = loginViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.registerStatus, state.value.isPermission) {
        if (state.value.isPermission) {
            navHostController.navigate(NavItem.IntroPermission.route) {
                popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            if (state.value.registerStatus == null) return@LaunchedEffect
            when (state.value.registerStatus) {
                RegisterStatus.FirstOnboarding -> {
                    navHostController.navigate(NavItem.FirstOnboarding.route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                RegisterStatus.TermsOfServices -> {
                    navHostController.navigate(NavItem.FirstOnboarding.route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                RegisterStatus.UserInfo -> {
                    navHostController.navigate(NavItem.SecondOnboarding.route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                RegisterStatus.CheckList -> {
                    navHostController.navigate(NavItem.FourthOnboarding.route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                RegisterStatus.ConsumeHabit -> {
                    navHostController.navigate(NavItem.FifthOnboarding.route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                RegisterStatus.ConsumePattern -> {
                    val route = NavItem.ChallengeAddition.route.replace("{isOnboarding}", "true")
                    navHostController.navigate(route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                RegisterStatus.AddChallenge -> {
                    navHostController.navigate(NavItem.Home.route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    BaseIcon(
                        modifier = Modifier.align(Alignment.Center),
                        iconId = R.drawable.illu_login_logo
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 45.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFEE500))
                    .noRippleClickable {
                        loginViewModel.loginWithKakaoTalk(context = context)
                    }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically) {

                    BaseIcon(iconId = R.drawable.ic_kakao_logo)
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.login_kakao),
                        style = goolbitgTypography.btn3,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(180.dp))
            }
        }
    }
}
