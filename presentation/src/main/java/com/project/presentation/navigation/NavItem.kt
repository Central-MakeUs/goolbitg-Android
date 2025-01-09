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
}