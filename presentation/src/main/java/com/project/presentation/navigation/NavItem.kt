package com.project.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.project.presentation.R

sealed class NavItem(
    val route: String,
    @DrawableRes val iconId: Int? = null,
    @StringRes val strId: Int? = null
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

    data object Home: NavItem(
        route = "home",
        iconId = R.drawable.ic_nav_home,
        strId = R.string.nav_item_home
    )

    data object Challenge: NavItem(
        route = "challenge",
        iconId = R.drawable.ic_nav_challenge,
        strId = R.string.nav_item_challenge
    )

    data object BuyOrNot: NavItem(
        route = "buy_or_not",
        iconId = R.drawable.ic_nav_buy_or_not,
        strId = R.string.nav_item_buy_or_not
    )

    data object MyPage: NavItem(
        route = "my_page",
        iconId = R.drawable.ic_nav_mypage,
        strId = R.string.nav_item_my_page
    )

    data object ChallengeAddition: NavItem(
        route = "challenge_addition/{isOnboarding}"
    )
    data object ChallengeDetail: NavItem(
        route = "challenge_detail/{challengeId}"
    )
}
