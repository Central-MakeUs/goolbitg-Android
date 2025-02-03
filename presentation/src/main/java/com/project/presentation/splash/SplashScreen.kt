package com.project.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.domain.model.user.RegisterStatus
import com.project.presentation.R
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1

@Composable
@Preview
fun SplashScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: SplashViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        val state = viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(state.value.registerStatus) {
            if (state.value.registerStatus == null) return@LaunchedEffect
            when (state.value.registerStatus) {
                RegisterStatus.Login -> {
                    navHostController.navigate(NavItem.Login.route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
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
                    navHostController.navigate(NavItem.ChallengeAddition.route) {
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
        Image(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(R.drawable.ic_splash),
            contentDescription = null
        )
    }
}
