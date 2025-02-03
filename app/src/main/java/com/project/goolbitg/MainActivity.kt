package com.project.goolbitg

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.messaging.FirebaseMessaging
import com.project.presentation.challenge.ChallengeScreen
import com.project.presentation.challenge.ChallengeAdditionScreen
import com.project.presentation.challenge.ChallengeDetailScreen
import com.project.presentation.home.HomeScreen
import com.project.presentation.login.LoginScreen
import com.project.presentation.mypage.MyPageScreen
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.screen.AnalysisConsumeTypeScreen
import com.project.presentation.onboarding.screen.FifthOnboardingScreen
import com.project.presentation.onboarding.screen.FirstOnboardingScreen
import com.project.presentation.onboarding.screen.FourthOnboardingScreen
import com.project.presentation.onboarding.screen.SecondOnboardingScreen
import com.project.presentation.onboarding.screen.ShowConsumeTypeScreen
import com.project.presentation.onboarding.screen.ThirdOnboardingScreenScreen
import com.project.presentation.permission.IntroPermissionScreen
import com.project.presentation.splash.SplashScreen
import com.project.presentation.ui.theme.bg1
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
    systemUiController.setNavigationBarColor(color = bg1, darkIcons = false)
    systemUiController.setSystemBarsColor(
        color = Transparent,
        darkIcons = false  // 아이콘 색상을 밝게 표시 (흰색)
    )

    NavHost(
        navController = navHostController,
        startDestination = NavItem.Splash.route,
        modifier = modifier
    ) {
        composable(NavItem.Splash.route) {
            SplashScreen(navHostController = navHostController)
        }

        composable(NavItem.Login.route) {
            LoginScreen(navHostController = navHostController)
        }

        composable(NavItem.IntroPermission.route) {
            IntroPermissionScreen(navHostController = navHostController)
        }

        composable(NavItem.FirstOnboarding.route) {
            FirstOnboardingScreen(navHostController = navHostController)
        }

        composable(NavItem.SecondOnboarding.route) {
            SecondOnboardingScreen(navHostController = navHostController)
        }

        composable(NavItem.ThirdOnboarding.route) {
            ThirdOnboardingScreenScreen(navHostController = navHostController)
        }

        composable(NavItem.FourthOnboarding.route) {
            FourthOnboardingScreen(navHostController = navHostController)
        }

        composable(NavItem.FifthOnboarding.route) {
            FifthOnboardingScreen(navHostController = navHostController)
        }

        composable(NavItem.AnalysisConsumeType.route) {
            AnalysisConsumeTypeScreen(navHostController = navHostController)
        }

        composable(NavItem.ShowConsumeType.route) {
            ShowConsumeTypeScreen(navHostController = navHostController)
        }

        composable(NavItem.Challenge.route) {
            ChallengeScreen(navHostController = navHostController)
        }
        composable(NavItem.Home.route) {
            HomeScreen(
                navHostController = navHostController,
            )
        }

        composable(NavItem.MyPage.route) {
            MyPageScreen(navHostController = navHostController)
        }

        composable(NavItem.ChallengeAddition.route) {
            ChallengeAdditionScreen(navHostController = navHostController)
        }

        composable(NavItem.ChallengeDetail.route) {
            ChallengeDetailScreen(navHostController = navHostController)
        }
    }
}


