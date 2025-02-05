package com.project.presentation.permission

import android.Manifest.permission.CAMERA
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.SET_ALARM
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.project.domain.model.user.RegisterStatus
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.login.LoginViewModel
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.spacingLg
import com.project.presentation.ui.theme.spacingMd
import com.project.presentation.ui.theme.spacingSm
import com.project.presentation.ui.theme.spacingXl
import com.project.presentation.ui.theme.spacingXs
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview(showBackground = true)
fun IntroPermissionScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.registerStatus) {
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

    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = mutableListOf(CAMERA).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(READ_MEDIA_IMAGES)
                add(POST_NOTIFICATIONS)
            } else {
                add(READ_EXTERNAL_STORAGE)
            }
        },
        onPermissionsResult = {
            viewModel.checkRegisterStatus()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            IntroPermissionBody(
                modifier = Modifier.padding(innerPadding),
                requestPermission = {
                    multiplePermissionState.launchMultiplePermissionRequest()
                }
            )
        }
    }
}

@Composable
fun IntroPermissionBody(
    modifier: Modifier = Modifier,
    requestPermission: () -> Unit
) {
    Column(modifier = modifier.padding(top = spacingXl, start = spacingMd, end = spacingMd)) {
        Text(
            modifier = Modifier.padding(horizontal = spacingSm),
            text = stringResource(R.string.permission_title),
            style = goolbitgTypography.h1,
            color = white
        )
        Spacer(modifier = Modifier.height(spacingLg))
        Text(
            modifier = Modifier.padding(horizontal = spacingSm),
            text = stringResource(R.string.permission_sub_title),
            style = goolbitgTypography.body1,
            color = gray300
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                items(
                    items = listOf(
                        PermissionData.Alarm,
                        PermissionData.Camera,
                        PermissionData.Album
                    )
                ) {
                    PermissionContentItem(permissionData = it, modifier = Modifier)
                }
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacingMd)
                .clip(CircleShape)
                .background(
                    brush = Brush.horizontalGradient(listOf(main100, Color(0xFF67BF4E)))
                )
                .clickable {
                    requestPermission()
                }
                .padding(vertical = 16.dp),
            text = stringResource(R.string.common_start),
            style = goolbitgTypography.btn1,
            color = white,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PermissionContentItem(
    permissionData: PermissionData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BaseIcon(
            iconId = permissionData.iconId,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(permissionData.titleId),
            style = goolbitgTypography.body2,
            color = gray100
        )
        Spacer(modifier = Modifier.height(spacingXs))
        Text(
            text = stringResource(permissionData.subTitleId),
            style = goolbitgTypography.body3,
            color = gray300
        )
    }
}

sealed class PermissionData(
    @DrawableRes val iconId: Int,
    @StringRes val titleId: Int,
    @StringRes val subTitleId: Int
) {
    data object Alarm : PermissionData(
        iconId = R.drawable.ic_permission_alarm,
        titleId = R.string.permission_alarm_title,
        subTitleId = R.string.permission_alarm_sub_title
    )

    data object Camera : PermissionData(
        iconId = R.drawable.ic_permission_camera,
        titleId = R.string.permission_camera_title,
        subTitleId = R.string.permission_camera_sub_title
    )

    data object Album : PermissionData(
        iconId = R.drawable.ic_permission_album,
        titleId = R.string.permission_album_title,
        subTitleId = R.string.permission_album_sub_title
    )
}
