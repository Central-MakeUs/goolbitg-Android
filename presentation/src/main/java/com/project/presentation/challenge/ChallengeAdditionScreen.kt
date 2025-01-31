package com.project.presentation.challenge

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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseIcon
import com.project.presentation.item.ChallengeInfo
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ChallengeAdditionScreen(navHostController: NavHostController = rememberNavController()) {
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
        mutableStateOf<ChallengeInfo?>(null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        // 콘텐츠 레이아웃
        BottomSheetScaffold(
            modifier = Modifier.drawBehind {
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
                            // TODO: 챌린지 도전
                            scaffoldState.bottomSheetState.partialExpand()
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
                        modifier = Modifier.size(32.dp, 4.dp)
                            .clip(CircleShape)
                            .background(gray400)
                    )
                }
            },
            sheetContentColor = gray600,
            sheetContainerColor = gray600,
            content = { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    ChallengeAdditionHeader(onBack = { navHostController.popBackStack() })
                    ChallengeAdditionContent(
                        nickname = "어린굴비",
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
                    )
                }
            }
        )
    }
}

@Composable
fun ChallengeAdditionHeader(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBack() }
                .padding(16.dp),
            iconId = R.drawable.ic_arrow_back
        )
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
    modifier: Modifier = Modifier,
    onChallengeClick: (ChallengeInfo) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.challenge_addition_title).replace("#VALUE#", nickname),
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

        val popularList = listOf(
            ChallengeInfo(id = 1, title = "야식 안시켜먹기", subTitle = "15,000원 절약 가능"),
            ChallengeInfo(id = 1, title = "택시 안타고 대중교통 이용하기", subTitle = "7,000원 절약 가능"),
            ChallengeInfo(id = 1, title = "야식 안시켜먹기", subTitle = "10,000원 절약 가능"),
        )
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
                popularList.forEachIndexed { idx, item ->
                    PopularChallengeItem(
                        rank = (idx + 1).toString(),
                        item = item,
                        onItemSelected = {
                            onChallengeClick(item)
                        }
                    )
                    if (idx != popularList.size - 1) {
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

        val etcList = listOf(
            ChallengeInfo(id = 1, title = "야식 안시켜먹기", subTitle = "15,000원 절약 가능"),
            ChallengeInfo(id = 2, title = "택시 안타고 대중교통 이용하기", subTitle = "7,000원 절약 가능"),
            ChallengeInfo(id = 3, title = "야식 안시켜먹기", subTitle = "10,000원 절약 가능"),
            ChallengeInfo(id = 3, title = "야식 안시켜먹기", subTitle = "10,000원 절약 가능"),
            ChallengeInfo(id = 3, title = "야식 안시켜먹기", subTitle = "10,000원 절약 가능"),
            ChallengeInfo(id = 3, title = "야식 안시켜먹기", subTitle = "10,000원 절약 가능"),
        )
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
                etcList.forEachIndexed { idx, item ->
                    EtcChallengeItem(
                        item = item,
                        onItemSelected = {
                            onChallengeClick(item)
                        }
                    )
                    if (idx != etcList.size - 1) {
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

@Composable
fun PopularChallengeItem(
    rank: String,
    item: ChallengeInfo,
    modifier: Modifier = Modifier,
    onItemSelected: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )  { onItemSelected() }
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = rank, style = goolbitgTypography.h1, color = main100)
        Spacer(modifier = Modifier.width(16.dp))
        BaseIcon(modifier = Modifier.size(45.dp), iconId = R.drawable.ic_challenge_default)
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.title, style = goolbitgTypography.body3, color = white)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.subTitle, style = goolbitgTypography.body4, color = gray300)
        }
        BaseIcon(iconId = R.drawable.ic_arrow_right_over)
    }
}

@Composable
fun EtcChallengeItem(
    item: ChallengeInfo,
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
        BaseIcon(modifier = Modifier.size(45.dp), iconId = R.drawable.ic_challenge_default)
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.title, style = goolbitgTypography.body3, color = white)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.subTitle, style = goolbitgTypography.body4, color = gray300)
        }
        BaseIcon(iconId = R.drawable.ic_arrow_right_over)
    }
}

@Composable
fun ChallengeBottomSheetContent(
    challengeInfo: ChallengeInfo?,
    modifier: Modifier = Modifier,
    onChallenge: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(gray500))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gray600)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseIcon(modifier = Modifier.size(160.dp), iconId = R.drawable.ic_challenge_default)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = challengeInfo?.title ?: "", style = goolbitgTypography.h3, color = gray50)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = challengeInfo?.subTitle ?: "", style = goolbitgTypography.body5, color = gray300)
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(gray500))
        BaseBottomBtn(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            text = stringResource(R.string.common_challenge),
            onClick = onChallenge
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

private fun calculateScrimAlpha(
    offset: Float,
    screenHeight: Int,
    bottomSheetMinOffset: Float
): Float {
    return ((screenHeight - offset) / (screenHeight - bottomSheetMinOffset))
        .coerceIn(0f, 1f) * 0.7f
}
