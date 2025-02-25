package com.project.presentation.buyornot.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseLoadingBox
import com.project.presentation.base.BaseSnackBar
import com.project.presentation.base.BaseTwoButtonPopup
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
@Preview(widthDp = 400, heightDp = 800)
fun BuyOrNotMyScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: BuyOrNotViewModel = hiltViewModel()
) {
    LaunchedEffect(navHostController.currentBackStackEntry?.savedStateHandle) {
        val savedStateHandle = navHostController.currentBackStackEntry?.savedStateHandle
        val postId = savedStateHandle?.get<Int>("postId")
        if (postId != null) {
            val imgUrl = savedStateHandle.get<String>("imgUrl") ?: ""
            val productName = savedStateHandle.get<String>("productName") ?: ""
            val price = savedStateHandle.get<Int>("price") ?: 0
            val goodReason = savedStateHandle.get<String>("goodReason") ?: ""
            val badReason = savedStateHandle.get<String>("badReason") ?: ""
            viewModel.onEvent(
                BuyOrNotEvent.ModifyLocalPosting(
                    postId = postId,
                    productName = productName,
                    imgUrl = imgUrl,
                    price = price,
                    goodReason = goodReason,
                    badReason = badReason
                )
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        val state = viewModel.state.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val snackBarHostState = remember { SnackbarHostState() }
        var deleteMyPostingTarget: BuyOrNotPostingModel? by remember { mutableStateOf(null) }
        val listState = rememberLazyListState()
        val context = LocalContext.current
        var backPressedState by remember { mutableStateOf(true) }
        var backPressedTime = 0L

        BackHandler {
            if (System.currentTimeMillis() - backPressedTime <= 1000L) {
                // 앱 종료
                (context as Activity).finish()
            } else {
                backPressedState = true
                coroutineScope.launch {
                    val job =
                        launch {
                            snackBarHostState.showSnackbar(
                                message = "종료하시려면 한 번 더 눌러주세요.",
                                withDismissAction = true,
                            )
                        }
                    delay(3000L)
                    job.cancel()
                }
            }
            backPressedTime = System.currentTimeMillis()
        }
        Scaffold(containerColor = transparent,
            bottomBar = {
                BaseBottomNavBar(navController = navHostController)
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    snackbar = {
                        BaseSnackBar(
                            modifier = Modifier.fillMaxWidth(),
                            bgColor = gray400,
                            snackBarData = it,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .consumeWindowInsets(WindowInsets.navigationBars)
                        .imePadding()
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                BuyOrNotMyHeader(
                    selectedTabIdx = 1,
                    onTabChanged = {
                        navHostController.navigate(NavItem.BuyOrNotMain.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onWritePosting = {
                        navHostController.navigate(NavItem.BuyOrNotAddPosting.route)
                    }
                )
                BuyOrNotCardMyContent(
                    modifier = Modifier.weight(1f),
                    listState = listState,
                    myPostingList = state.value.myPostList,
                    isLoading = state.value.isMyPostsSkeletonLoading,
                    pageOffset = state.value.myPostPage * state.value.myPostSize,
                    onMyItemClick = {},
                    onFetchNextPage = { viewModel.onEvent(BuyOrNotEvent.FetchMyNextPage) },
                    onActiveDeleteMyPostingPopup = { deleteMyPostingTarget = it },
                    onModifyMyPosting = {
                        val route = NavItem.BuyOrNotModifyPosting.route
                            .replace("{postId}", it.id.toString())
                            .replace("{productName}", it.productName)
                            .replace("{price}", it.productPrice.toString())
                            .replace(
                                "{imgUrl}", URLEncoder.encode(
                                    it.productImageUrl,
                                    StandardCharsets.UTF_8.toString()
                                )
                            )
                            .replace("{goodReason}", it.goodReason)
                            .replace("{badReason}", it.badReason)
                        navHostController.navigate(route)
                    },
                    onAddPosting = {
                        navHostController.navigate(NavItem.BuyOrNotAddPosting.route)
                    }
                )
            }
        }
        if (deleteMyPostingTarget != null) {
            BaseTwoButtonPopup(
                title = stringResource(R.string.common_delete),
                subTitle = stringResource(R.string.buyornot_delete_popup_sub_title),
                confirmText = stringResource(R.string.common_delete_2),
                dismissText = stringResource(R.string.common_cancel),
                onConfirm = {
                    viewModel.onEvent(BuyOrNotEvent.DeleteMyPosting(deleteMyPostingTarget!!.id))
                    deleteMyPostingTarget = null
                },
                onDismiss = { deleteMyPostingTarget = null }
            )
        }
        if(state.value.isMyPostsLoading){
            BaseLoadingBox()
        }
    }
}

@Composable
fun BuyOrNotMyHeader(
    modifier: Modifier = Modifier,
    selectedTabIdx: Int,
    onTabChanged: (Int) -> Unit,
    onWritePosting: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier.noRippleClickable { if (selectedTabIdx != 0) onTabChanged(0) },
                text = stringResource(R.string.buyornot_header_title_1),
                style = goolbitgTypography.h1,
                color = if (selectedTabIdx == 0) white else gray400
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier.noRippleClickable { if (selectedTabIdx != 1) onTabChanged(1) },
                text = stringResource(R.string.buyornot_header_title_2),
                style = goolbitgTypography.h1,
                color = if (selectedTabIdx == 1) white else gray400
            )
        }

        BaseIcon(
            modifier = Modifier
                .noRippleClickable {
                    onWritePosting()
                }
                .padding(8.dp),
            iconId = R.drawable.ic_pencil
        )
    }
}
