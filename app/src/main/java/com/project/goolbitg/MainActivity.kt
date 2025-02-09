package com.project.goolbitg

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.messaging.FirebaseMessaging
import com.project.presentation.challenge.main.ChallengeScreen
import com.project.presentation.challenge.addition.ChallengeAdditionScreen
import com.project.presentation.challenge.detail.ChallengeDetailScreen
import com.project.presentation.challenge.addition.ChallengeAdditionEvent
import com.project.presentation.challenge.addition.ChallengeAdditionViewModel
import com.project.presentation.challenge.detail.ChallengeDetailEvent
import com.project.presentation.challenge.detail.ChallengeDetailViewModel
import com.project.presentation.home.HomeScreen
import com.project.presentation.login.LoginScreen
import com.project.presentation.mypage.MyPageScreen
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.onboarding.screen.AnalysisConsumeTypeScreen
import com.project.presentation.onboarding.screen.FifthOnboardingScreen
import com.project.presentation.onboarding.screen.FirstOnboardingScreen
import com.project.presentation.onboarding.screen.FourthOnboardingScreen
import com.project.presentation.onboarding.screen.SecondOnboardingScreen
import com.project.presentation.onboarding.screen.ShowConsumeTypeScreen
import com.project.presentation.onboarding.screen.ThirdOnboardingScreenScreen
import com.project.presentation.permission.IntroPermissionScreen
import com.project.presentation.splash.SplashScreen
import com.project.presentation.ui.theme.gray800
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 시스템 UI 콘텐츠 위로 확장
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 하단바 숨기기
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.isAppearanceLightStatusBars = false // 상태바 아이콘 색상 설정
        }
        initFcmToken()
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            NavigationGraph(navHostController = navHostController)
        }
    }
}

private fun initFcmToken() {
    // 로그인 하지 않은 경우 제외
    // 로그인 상태인데 유효한 토큰이 설정된 적이 없는 경우 Firebase 에서 등록된 FcmToken 을 가져오고 서버로 전송한다.
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) return@addOnCompleteListener
        val fcmToken = task.result
        Log.d("TAG", "initFcmToken: ${fcmToken}")
    }
}

@Composable
private fun NavigationGraph(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val systemUiController = rememberSystemUiController()

    // 상태바와 내비게이션 바 색상 설정
    systemUiController.setSystemBarsColor(
        color = gray800,
        darkIcons = false  // 아이콘 색상을 밝게 표시 (흰색)
    )

    NavHost(
        navController = navHostController,
        startDestination = NavItem.Splash.route,
        modifier = modifier
    ) {
        directComposable(NavItem.Splash.route) {
            SplashScreen(navHostController = navHostController)
        }

        directComposable(NavItem.Login.route) {
            LoginScreen(navHostController = navHostController)
        }

        directComposable(NavItem.IntroPermission.route) {
            IntroPermissionScreen(navHostController = navHostController)
        }

        addComposable(NavItem.FirstOnboarding.route) { backStackEntry ->
            val viewModel: OnboardingViewModel = hiltViewModel(backStackEntry)
            FirstOnboardingScreen(navHostController = navHostController, viewModel = viewModel)
        }

        addComposable(NavItem.SecondOnboarding.route) {
            val viewModel: OnboardingViewModel = if(navHostController.previousBackStackEntry != null){
                hiltViewModel(navHostController.previousBackStackEntry!!)
            }else{
                hiltViewModel()
            }
            SecondOnboardingScreen(navHostController = navHostController, viewModel = viewModel)
        }

        addComposable(NavItem.ThirdOnboarding.route) {
            ThirdOnboardingScreenScreen(navHostController = navHostController)
        }

        addComposable(NavItem.FourthOnboarding.route) {
            FourthOnboardingScreen(navHostController = navHostController)
        }

        addComposable(NavItem.FifthOnboarding.route) {
            FifthOnboardingScreen(navHostController = navHostController)
        }

        addComposable(NavItem.AnalysisConsumeType.route) {
            AnalysisConsumeTypeScreen(navHostController = navHostController)
        }

        addComposable(NavItem.ShowConsumeType.route) {
            ShowConsumeTypeScreen(navHostController = navHostController)
        }

        directComposable(NavItem.Home.route) {
            HomeScreen(navHostController = navHostController)
        }

        directComposable(NavItem.Challenge.route) {
            ChallengeScreen(navHostController = navHostController)
        }

        directComposable(NavItem.MyPage.route) {
            MyPageScreen(navHostController = navHostController)
        }

        addComposable(
            route = NavItem.ChallengeAddition.route,
            arguments = listOf(navArgument("isOnboarding") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isOnboarding = backStackEntry.arguments?.getBoolean("isOnboarding") ?: true
            val viewModel: ChallengeAdditionViewModel = hiltViewModel()
            viewModel.onEvent(ChallengeAdditionEvent.BackPressOption(!isOnboarding))
            ChallengeAdditionScreen(navHostController = navHostController, viewModel = viewModel)
        }

        addComposable(
            route = NavItem.ChallengeDetail.route,
            arguments = listOf(navArgument("challengeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getInt("challengeId")
            val viewModel: ChallengeDetailViewModel = hiltViewModel()
            if(challengeId != null){
                viewModel.onEvent(ChallengeDetailEvent.InitChallengeId(challengeId))
            }
            ChallengeDetailScreen(navHostController = navHostController, viewModel = viewModel)
        }
    }

}

fun NavGraphBuilder.directComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
){
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = null,
        exitTransition = null,
        popEnterTransition = null,
        popExitTransition = null,
        content = content
    )
}

fun NavGraphBuilder.addComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
){
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) },
        content = content
    )
}

