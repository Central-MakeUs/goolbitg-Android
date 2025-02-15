package com.project.presentation.buyornot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTintIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.base.extension.StringExtension.priceComma
import com.project.presentation.challenge.addition.calculateScrimAlpha
import com.project.presentation.challenge.addition.getScreenHeightInPixelsOnce
import com.project.presentation.item.ReportReason
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.main15
import com.project.presentation.ui.theme.main20
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.spacingLg
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(widthDp = 400, heightDp = 800)
fun BuyOrNotScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: BuyOrNotViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

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
        val coroutineScope = rememberCoroutineScope()
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
                    }
                ) { innerPadding ->
                    BuyOrNotContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        postingList = state.value.buyOrNotPostList,
                        isLoading = state.value.isLoading,
                        pageOffset = state.value.page * state.value.size,
                        onOpenReportSheet = {
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        onFetchNextPage = {
                            viewModel.onEvent(BuyOrNotEvent.FetchNextPage)
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
    }
}

@Composable
fun BuyOrNotContent(
    modifier: Modifier = Modifier,
    postingList: List<BuyOrNotPostingModel>,
    isLoading: Boolean,
    pageOffset: Int,
    onOpenReportSheet: () -> Unit,
    onFetchNextPage: () -> Unit
) {
    Column(modifier = modifier) {
        BuyOrNotHeader(
            onWritePosting = {

            }
        )
        BuyOrNotCardContent(
            modifier = Modifier.weight(1f),
            postingList = postingList,
            isLoading = isLoading,
            pageOffset = pageOffset,
            onOpenReportSheet = onOpenReportSheet,
            onFetchNextPage = onFetchNextPage
        )
        BuyOrNotGoodOrBad(modifier = Modifier.padding(top = 24.dp, bottom = 21.dp))
    }
}

@Composable
fun BuyOrNotHeader(
    modifier: Modifier = Modifier,
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
                text = stringResource(R.string.buyornot_header_title_1),
                style = goolbitgTypography.h1,
                color = white
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.buyornot_header_title_2),
                style = goolbitgTypography.h1,
                color = white
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

@Composable
fun BuyOrNotCardContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    pageOffset: Int,
    postingList: List<BuyOrNotPostingModel>,
    onOpenReportSheet: () -> Unit,
    onFetchNextPage: () -> Unit
) {
    val pagerState = rememberPagerState { postingList.size }
    HorizontalPager(
        modifier = modifier.fillMaxWidth(),
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 30.dp)
    ) { page ->

        LaunchedEffect(page) {
            if(!isLoading && page > pageOffset - 5){
                onFetchNextPage()
            }
        }

        if (page < postingList.size) {
            BuyOrNotCard(
                modifier = Modifier.carouselTransition(page = page, pagerState = pagerState),
                posting = postingList[page],
                onOpenReportSheet = onOpenReportSheet
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BuyOrNotCard(
    modifier: Modifier = Modifier,
    posting: BuyOrNotPostingModel,
    onOpenReportSheet: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp)
                .clip(RoundedCornerShape(spacingLg))
                .border(
                    width = 1.dp,
                    color = white.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(spacingLg)
                )
                .background(Color(0x0DFFFFFF))
                .padding(horizontal = 24.dp, vertical = 15.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier.noRippleClickable {
                        onOpenReportSheet()
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BaseTintIcon(iconId = R.drawable.ic_triangle_caution, tint = gray400)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.buyornot_report),
                        style = goolbitgTypography.body5,
                        color = gray400
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                GlideImage(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(roundSm)),
                    model = posting.productImageUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = posting.productName, style = goolbitgTypography.body4, color = white)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = posting.productPrice.priceComma(), style = goolbitgTypography.h3, color = white)
            }
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(white.copy(alpha = 0.1f))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseIcon(iconId = R.drawable.ic_good)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = posting.goodReason,
                    style = goolbitgTypography.body4,
                    color = white,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseIcon(iconId = R.drawable.ic_bad)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = posting.badReason,
                    style = goolbitgTypography.body4,
                    color = white,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
@Preview
fun BuyOrNotGoodOrBad(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseIcon(iconId = R.drawable.ic_thumbs_up)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "??", style = goolbitgTypography.caption2, color = gray400)
        }
        Spacer(modifier = Modifier.width(40.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseIcon(iconId = R.drawable.ic_thumbs_down)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "??", style = goolbitgTypography.caption2, color = gray400)
        }
    }
}

@Composable
fun ReportBottomSheetContent(
    modifier: Modifier = Modifier,
    reasonList: List<ReportReason>,
    onItemClick: (Int) -> Unit,
    onReport: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(gray600),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.buyornot_report), style = goolbitgTypography.h3, color = gray50)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.buyornot_report_sub_title),
            style = goolbitgTypography.body5,
            color = gray200
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(gray500)
        )
        reasonList.forEachIndexed { idx, item ->
            val (bgColor, textColor, borderColor) = if (item.isSelected) {
                Triple(main20, main100, main15)
            } else {
                Triple(gray600, white, gray400)
            }
            Row(
                modifier = modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .border(width = 1.dp, shape = CircleShape, color = borderColor)
                    .background(bgColor)
                    .noRippleClickable { onItemClick(idx) }
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                BaseIcon(iconId = if (item.isSelected) R.drawable.ic_checkbox_green else R.drawable.ic_checkbox_gray)

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(item.reasonEnum.strId),
                    style = goolbitgTypography.caption2,
                    color = textColor
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(gray500)
        )
        BaseBottomBtn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.buyornot_report),
            onClick = onReport
        )

        // 이거 안해주면 시스템 네비게이션바랑 겹침
        Spacer(
            modifier =
            Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
        )
    }
}

@Composable
fun Modifier.carouselTransition(page: Int, pagerState: PagerState) =
    graphicsLayer {
        val pageOffset =
            ((pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue)
        val transformation =
            lerp(
                start = 0.8f,
                stop = 1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f)
            )
        alpha = transformation
        scaleY = transformation
    }
