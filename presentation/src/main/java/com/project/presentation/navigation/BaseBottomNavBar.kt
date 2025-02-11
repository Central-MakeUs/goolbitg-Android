package com.project.presentation.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.presentation.base.BaseIcon
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.roundLg

/**
 * 기본으로 들어가는 바텀 네비게이션
 * @author 이유호
 * @param navController 바텀 네비게이션 조작을 위한 NavHostController
 * @param items 바텀 네비게이션에 들어갈 아이템, default: list(Home, Challenge, BuyOrNot, MyPage)
 */
@Composable
fun BaseBottomNavBar(
    navController: NavHostController = rememberNavController(),
    items: List<NavItem> = listOf(
        NavItem.Home,
        NavItem.Challenge,
        NavItem.MyPage,
    )
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = roundLg, topEnd = roundLg))
            .border(width = 1.dp, color = gray400, shape = RoundedCornerShape(topStart = roundLg, topEnd = roundLg)),
        containerColor = gray600,
        contentColor = gray400
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            val isSelected = currentDestination?.route == item.route
            NavigationBarItem(
                selected = isSelected,
                icon = {
                    if (item.iconId != null) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(item.iconId),
                            contentDescription = null
                        )
                    }
                },
                label = {
                    if (item.strId != null) {
                        val style = goolbitgTypography.body5.apply {
                            if (currentDestination?.route == item.route) {
                                copy(fontWeight = FontWeight.Bold)
                            } else {
                                copy(fontWeight = FontWeight.Normal)
                            }
                        }
                        Text(
                            text = stringResource(id = item.strId),
                            style = style,
                        )
                    }
                },
                onClick = {
                    if(!isSelected){
                        navController.navigate(item.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = main100,
                    unselectedIconColor = gray400,
                    selectedTextColor = main100,
                    unselectedTextColor = gray400,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
