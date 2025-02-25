package com.project.presentation.buyornot.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseSnackBar
import com.project.presentation.base.BaseTwoButtonPopup
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.challenge.addition.calculateScrimAlpha
import com.project.presentation.challenge.addition.getScreenHeightInPixelsOnce
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(widthDp = 400, heightDp = 800)
fun BuyOrNotScreen(
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
        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Hidden,
                skipHiddenState = false
            )
        )
        var bottomSheetScrimAlpha by remember {
            mutableFloatStateOf(0f)
        }
        val isBottomSheetScrimVisible by remember {
            derivedStateOf {
                bottomSheetScrimAlpha > 0
            }
        }
        val screenHeight = getScreenHeightInPixelsOnce()

        val bottomSheetContentHeightInPx by remember {
            mutableFloatStateOf(434f)
        }
        val bottomSheetDragHandleHeightInPx by remember {
            mutableFloatStateOf(29f)
        }

        val state = viewModel.state.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val snackBarHostState = remember { SnackbarHostState() }

        var deleteMyPostingTarget: BuyOrNotPostingModel? by remember { mutableStateOf(null) }

        LaunchedEffect(state.value.reportResult) {
            if (!state.value.reportResult.isNullOrEmpty()) {
                coroutineScope.launch {
                    val job =
                        launch {
                            snackBarHostState.showSnackbar(
                                message = state.value.reportResult!!,
                                withDismissAction = true,
                            )
                        }
                    delay(3000L)
                    job.cancel()
                }
                withContext(Dispatchers.Main) {
                    viewModel.initReportResult()
                }
            }
        }

        var reportPosting by remember { mutableStateOf<BuyOrNotPostingModel?>(null) }
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
        BottomSheetScaffold(
            modifier = Modifier
                .drawBehind {
                    val offset = scaffoldState.bottomSheetState.requireOffset() // 바텀시트 현재 오프셋 값
                    val bottomSheetMinOffset =
                        screenHeight - bottomSheetContentHeightInPx - bottomSheetDragHandleHeightInPx // 바텀시트가 가질 수 있는 최소 offset 값
                    bottomSheetScrimAlpha = calculateScrimAlpha(
                        offset = offset,
                        screenHeight = screenHeight,
                        bottomSheetMinOffset = bottomSheetMinOffset
                    )
                },
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = transparent,
            sheetSwipeEnabled = false,
            sheetContent = {
                ReportBottomSheetContent(
                    modifier = Modifier.fillMaxWidth(),
                    reasonList = state.value.reportList,
                    onItemClick = { idx ->
                        viewModel.onEvent(BuyOrNotEvent.ClickReportReason(idx))
                    },
                    onReport = {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.partialExpand()
                            viewModel.onEvent(BuyOrNotEvent.ReportPosting(postId = reportPosting!!.id, reason = it))
                            reportPosting = null
                        }
                    }
                )
            },
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(gray600)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                if (delta > 50f) {
                                    // 드래그 이동 처리
                                    coroutineScope.launch {
                                        scaffoldState.bottomSheetState.partialExpand()
                                        viewModel.initReportList()
                                        reportPosting = null
                                    }
                                }
                            }
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp, 4.dp)
                            .clip(CircleShape)
                            .background(gray400)
                    )
                }
            },
            sheetContentColor = gray600,
            sheetContainerColor = gray600,
            content = {
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
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .consumeWindowInsets(WindowInsets.navigationBars)
                                .imePadding()
                        )
                    }
                ) { innerPadding ->
                    BuyOrNotContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        selectedTabIdx = state.value.tabIdx,
                        mainPostingList = state.value.mainPostList,
                        myPostingList = state.value.myPostList,
                        isMainLoading = state.value.isMainPostsLoading,
                        isMyLoading = state.value.isMyPostsLoading,
                        mainPageOffset = state.value.mainPostPage * state.value.mainPostSize,
                        myPageOffset = state.value.myPostPage * state.value.myPostSize,
                        onOpenReportSheet = {
                            coroutineScope.launch {
                                reportPosting = it
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        onFetchMainNextPage = {
                            viewModel.onEvent(BuyOrNotEvent.FetchMainNextPage)
                        },
                        onFetchMyNextPage = {
                            viewModel.onEvent(BuyOrNotEvent.FetchMyNextPage)
                        },
                        onActiveDeleteMyPostingPopup = {
                            deleteMyPostingTarget = it
                        },
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
                        onWritePosting = {
                            val route = NavItem.BuyOrNotAddPosting.route
                                .replace("{tabIdx}", state.value.tabIdx.toString())
                            navHostController.navigate(route)
                        },
                        onVote = { postId, isGood ->
                            viewModel.onEvent(BuyOrNotEvent.VotePosting(postId, isGood))
                        },
                        onTabChanged = {
                            viewModel.setTabIdx(it)
                        }
                    )
                }
                if (isBottomSheetScrimVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                drawRect(black.copy(alpha = bottomSheetScrimAlpha))
                            }
                            .noRippleClickable {}
                    )
                }
            }
        )

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
    }
}

@Composable
fun BuyOrNotContent(
    modifier: Modifier = Modifier,
    selectedTabIdx: Int,
    mainPostingList: List<BuyOrNotPostingModel>?,
    myPostingList: List<BuyOrNotPostingModel>?,
    isMainLoading: Boolean,
    isMyLoading: Boolean,
    mainPageOffset: Int,
    myPageOffset: Int,
    onOpenReportSheet: (BuyOrNotPostingModel) -> Unit,
    onFetchMainNextPage: () -> Unit,
    onFetchMyNextPage: () -> Unit,
    onActiveDeleteMyPostingPopup: (BuyOrNotPostingModel) -> Unit,
    onModifyMyPosting: (BuyOrNotPostingModel) -> Unit,
    onWritePosting: () -> Unit,
    onVote: (Int, Boolean) -> Unit,
    onTabChanged: (Int) -> Unit
) {
    Column(modifier = modifier) {
        BuyOrNotHeader(
            selectedTabIdx = selectedTabIdx,
            onTabChanged = onTabChanged,
            onWritePosting = onWritePosting
        )
        val pagerState = rememberPagerState { mainPostingList?.size ?: 0 }
        val listState = rememberLazyListState()

        when (selectedTabIdx) {
            0 -> {
                BuyOrNotCardMainContent(
                    modifier = Modifier.weight(1f),
                    pagerState = pagerState,
                    postingList = mainPostingList,
                    isLoading = isMainLoading,
                    pageOffset = mainPageOffset,
                    onOpenReportSheet = onOpenReportSheet,
                    onFetchNextPage = onFetchMainNextPage,
                    onVote = onVote
                )
            }
            1 -> {
                BuyOrNotCardMyContent(
                    modifier = Modifier.weight(1f),
                    listState = listState,
                    myPostingList = myPostingList,
                    isLoading = isMyLoading,
                    pageOffset = myPageOffset,
                    onMyItemClick = {},
                    onFetchNextPage = onFetchMyNextPage,
                    onActiveDeleteMyPostingPopup = onActiveDeleteMyPostingPopup,
                    onModifyMyPosting = onModifyMyPosting,
                    onAddPosting = onWritePosting
                )
            }
            else -> Unit
        }
    }
}

@Composable
fun BuyOrNotHeader(
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


