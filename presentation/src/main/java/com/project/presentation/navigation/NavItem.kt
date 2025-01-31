package com.project.presentation.navigation

sealed class NavItem(
    val route: String
) {
    data object Splash: NavItem(
        route = "splash"
    )

    data object IntroPermission: NavItem(
        route = "intro_permission"
    )

    data object Login: NavItem(
        route = "login"
    )

    data object FirstOnboarding: NavItem(
        route = "first_onboarding"
    )

    data object SecondOnboarding: NavItem(
        route = "Second_onboarding"
    )

    data object ThirdOnboarding: NavItem(
        route = "third_onboarding"
    )

    data object FourthOnboarding: NavItem(
        route = "fourth_onboarding"
    )

    data object FifthOnboarding: NavItem(
        route = "fifth_onboarding"
    )

    data object AnalysisConsumeType: NavItem(
        route = "analysis_consume_type"
    )

    data object ShowConsumeType: NavItem(
        route = "show_consume_type"
    )

    data object Challenge: NavItem(
        route = "challenge"
    )
}