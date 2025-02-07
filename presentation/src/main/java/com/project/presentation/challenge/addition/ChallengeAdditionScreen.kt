package com.project.presentation.challenge.addition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.project.domain.model.challenge.ChallengeInfoModel
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.fadingEdge
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ChallengeAdditionScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: ChallengeAdditionViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.isEnrollSuccess) {
        if (state.value.isEnrollSuccess) {
            if (state.value.isBackEnabled) {
                navHostController.popBackStack()
            } else {
                navHostController.navigate(NavItem.Challenge.route) {
                    popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

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

    var selectedChallenge by remember {
        mutableStateOf<ChallengeInfoModel?>(null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            // 콘텐츠 레이아웃
            BottomSheetScaffold(
                modifier = Modifier
                    .padding(innerPadding)
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
                    ChallengeBottomSheetContent(
                        modifier = Modifier.fillMaxWidth(),
                        challengeInfo = selectedChallenge,
                        onChallenge = {
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.partialExpand()
                                selectedChallenge?.let {
                                    viewModel.onEvent(
                                        ChallengeAdditionEvent.EnrollChallenge(
                                            challengeId = it.id
                                        )
                                    )
                                }
                                selectedChallenge = null
                            }
                        },
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
                                            selectedChallenge = null
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
                content = { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        ChallengeAdditionHeader(
                            isBackEnabled = state.value.isBackEnabled,
                            onBack = { navHostController.popBackStack() }
                        )
                        ChallengeAdditionContent(
                            nickname = state.value.nickname,
                            popularChallengeList = state.value.popularChallengeList,
                            etcChallengeList = state.value.etcChallengeList,
                            isLoading = state.value.isLoading,
                            modifier = Modifier.weight(1f),
                            onChallengeClick = {
                                coroutineScope.launch {
                                    selectedChallenge = it
                                    scaffoldState.bottomSheetState.expand()
                                }
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
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {}
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ChallengeAdditionHeader(
    modifier: Modifier = Modifier,
    isBackEnabled: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isBackEnabled) {
            BaseIcon(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBack() }
                    .padding(16.dp),
                iconId = R.drawable.ic_arrow_back
            )
        } else {
            Spacer(modifier = Modifier.size(64.dp))
        }
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.challenge_addition_header_title),
            style = goolbitgTypography.h3,
            color = white,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(64.dp))
    }
}

@Composable
fun ChallengeAdditionContent(
    nickname: String,
    popularChallengeList: List<ChallengeInfoModel>,
    etcChallengeList: List<ChallengeInfoModel>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onChallengeClick: (ChallengeInfoModel) -> Unit
) {
    val listState = rememberLazyListState()
    // 첫 번째 아이템과 마지막 아이템 가시성을 추적하는 상태
    val showTopFade by remember {
        derivedStateOf { listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0 }
    }
    val showBottomFade by remember {
        derivedStateOf {
            (listState.layoutInfo.totalItemsCount > 0 &&
                    listState.layoutInfo.visibleItemsInfo.lastOrNull()?.let { lastItem ->
                        lastItem.index == listState.layoutInfo.totalItemsCount - 1 &&
                                lastItem.offset + lastItem.size <= listState.layoutInfo.viewportEndOffset
                    } ?: true).not()
        }
    }

    val listFade by remember {
        derivedStateOf {
            when {
                showTopFade && showBottomFade -> Brush.verticalGradient(
                    0f to transparent,
                    0.05f to white,
                    0.95f to white,
                    1f to transparent
                )

                showTopFade -> Brush.verticalGradient(0f to transparent, 0.05f to white)
                showBottomFade -> Brush.verticalGradient(0.95f to white, 1f to transparent)
                else -> null
            }
        }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (listFade != null) {
                    Modifier.fadingEdge(listFade!!)
                } else {
                    Modifier
                }
            ).padding(horizontal = 24.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        state = listState
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.challenge_addition_title).replace(
                    "#VALUE#",
                    nickname
                ),
                color = white,
                style = goolbitgTypography.h1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.challenge_addition_sub_title),
                color = gray300,
                style = goolbitgTypography.body2
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(width = 1.dp, shape = RoundedCornerShape(8.dp), color = gray500)
                    .background(gray600)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.challenge_addition_same_consume_type_challenge),
                    style = goolbitgTypography.h4,
                    color = white
                )
                if(isLoading){
                    (1..3).forEach {
                        PopularChallengeLoadingItem(
                            rank = it.toString(),
                            alpha = (4 - it) / 3f,
                        )
                        if (it != 3) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .height(1.dp)
                                    .background(gray500)
                            )
                        }
                    }
                }else{
                    popularChallengeList.forEachIndexed { idx, item ->
                        PopularChallengeItem(
                            rank = (idx + 1).toString(),
                            alpha = (popularChallengeList.size - idx) / popularChallengeList.size.toFloat(),
                            item = item,
                            onItemSelected = {
                                onChallengeClick(item)
                            }
                        )
                        if (idx != popularChallengeList.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .height(1.dp)
                                    .background(gray500)
                            )
                        }
                    }
                }

            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(width = 1.dp, shape = RoundedCornerShape(8.dp), color = gray500)
                    .background(gray600)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.challenge_addition_etc_challenge),
                    style = goolbitgTypography.h4,
                    color = white
                )
                if(isLoading){
                    (1..10).forEach {
                        EtcChallengeLoadingItem()
                        if (it != 10) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .height(1.dp)
                                    .background(gray500)
                            )
                        }
                    }
                }else{
                    etcChallengeList.forEachIndexed { idx, item ->
                        EtcChallengeItem(
                            item = item,
                            onItemSelected = {
                                onChallengeClick(item)
                            }
                        )
                        if (idx != etcChallengeList.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .height(1.dp)
                                    .background(gray500)
                            )
                        }
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PopularChallengeItem(
    rank: String,
    alpha: Float,
    item: ChallengeInfoModel,
    modifier: Modifier = Modifier,
    onItemSelected: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onItemSelected() }
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = rank, style = goolbitgTypography.h1, color = main100.copy(alpha = alpha))
        Spacer(modifier = Modifier.width(16.dp))
        if (item.imageUrlSmall.isEmpty()) {
            BaseIcon(modifier = Modifier.size(45.dp), iconId = R.drawable.ic_challenge_default)
        } else {
            GlideImage(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape),
                model = item.imageUrlSmall,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.title, style = goolbitgTypography.body3, color = white)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.challenge_addition_item_sub_title)
                    .replace("#VALUE#", item.reward.toString()),
                style = goolbitgTypography.body4, color = gray300
            )
        }
        BaseIcon(iconId = R.drawable.ic_arrow_right_over)
    }
}

@Composable
fun PopularChallengeLoadingItem(
    rank: String,
    alpha: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = rank, style = goolbitgTypography.h1, color = main100.copy(alpha = alpha))
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier
            .size(45.dp)
            .clip(CircleShape)
            .shimmer()
            .background(gray500))
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .shimmer()
                    .background(white),
                text = "",
                style = goolbitgTypography.body3,
                color = white
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .shimmer()
                    .background(white),
                text = "",
                style = goolbitgTypography.body4, color = gray300
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EtcChallengeItem(
    item: ChallengeInfoModel,
    modifier: Modifier = Modifier,
    onItemSelected: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onItemSelected()
            }
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.imageUrlSmall.isEmpty()) {
            BaseIcon(modifier = Modifier.size(45.dp), iconId = R.drawable.ic_challenge_default)
        } else {
            GlideImage(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape),
                model = item.imageUrlSmall,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.title, style = goolbitgTypography.body3, color = white)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.challenge_addition_item_sub_title)
                    .replace("#VALUE#", item.reward.toString()),
                style = goolbitgTypography.body4, color = gray300
            )
        }
        BaseIcon(iconId = R.drawable.ic_arrow_right_over)
    }
}

@Composable
fun EtcChallengeLoadingItem(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .size(45.dp)
            .clip(CircleShape)
            .shimmer()
            .background(gray500))
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .shimmer()
                    .background(white),
                text = "",
                style = goolbitgTypography.body3,
                color = white
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .shimmer()
                    .background(white),
                text = "",
                style = goolbitgTypography.body4, color = gray300
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ChallengeBottomSheetContent(
    challengeInfo: ChallengeInfoModel?,
    modifier: Modifier = Modifier,
    onChallenge: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(gray500)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gray600)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (challengeInfo?.imageUrlSmall.isNullOrEmpty()) {
                BaseIcon(modifier = Modifier.size(160.dp), iconId = R.drawable.ic_challenge_default)
            } else {
                GlideImage(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape),
                    model = challengeInfo?.imageUrlLarge ?: "",
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = challengeInfo?.title ?: "", style = goolbitgTypography.h3, color = gray50)
            Spacer(modifier = Modifier.height(4.dp))

            if (challengeInfo != null) {
                Text(
                    text = stringResource(R.string.challenge_addition_item_sub_title)
                        .replace("#VALUE#", challengeInfo.reward.toString()),
                    style = goolbitgTypography.body5,
                    color = gray300
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(gray500)
        )
        BaseBottomBtn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(R.string.common_challenge),
            onClick = onChallenge
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
fun getScreenHeightInPixelsOnce(): Int {
    val context = LocalContext.current
    return remember {
        val displayMetrics = context.resources.displayMetrics
        displayMetrics.heightPixels
    }
}

fun calculateScrimAlpha(
    offset: Float,
    screenHeight: Int,
    bottomSheetMinOffset: Float
): Float {
    return ((screenHeight - offset) / (screenHeight - bottomSheetMinOffset))
        .coerceIn(0f, 1f) * 0.7f
}
