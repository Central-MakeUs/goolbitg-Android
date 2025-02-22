package com.project.presentation.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.fadingEdge
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.CheckListData
import com.project.presentation.onboarding.OnboardingEvent
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.main15
import com.project.presentation.ui.theme.main20
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@Composable
@Preview
fun ThirdOnboardingScreenScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val isNextBtnVisible by remember(state.value) {
        derivedStateOf {
            state.value.isThirdOnboardingCompleted()
        }
    }

    LaunchedEffect(state.value.isCheckListSuccess) {
        if(state.value.isCheckListSuccess){
            navHostController.navigate(NavItem.FourthOnboarding.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            ThirdOnboardingBody(
                checkList = state.value.checkList,
                isButtonVisible = isNextBtnVisible,
                modifier = Modifier.padding(innerPadding),
                onItemClick = { idx ->
                    viewModel.onEvent(OnboardingEvent.ClickCheckListItem(idx = idx))
                },
                onNext = {
                    viewModel.onEvent(OnboardingEvent.RequestSetUserCheckList)
                }
            )
        }
    }

}

@Composable
fun ThirdOnboardingBody(
    checkList: List<CheckListData>,
    isButtonVisible: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
    ) {

        val text = stringResource(R.string.onboarding_third_title)
        val highlightWord = stringResource(R.string.onboarding_third_sub_title_highlight)
        val annotatedString = buildAnnotatedString {
            val startIndex = text.indexOf(highlightWord)
            append(text)
            if (startIndex != -1) {
                addStyle(
                    style = SpanStyle(color = Color(0xFF4BB329)),
                    start = startIndex,
                    end = startIndex + highlightWord.length
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = annotatedString,
                style = goolbitgTypography.h1,
                color = white
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.onboarding_third_sub_title),
                style = goolbitgTypography.body1,
                color = gray300
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                            0.85f to white,
                            1f to transparent
                        )

                        showTopFade -> Brush.verticalGradient(0f to transparent, 0.05f to white)
                        showBottomFade -> Brush.verticalGradient(0.85f to white, 1f to transparent)
                        else -> null
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (listFade != null) {
                            Modifier.fadingEdge(listFade!!)
                        } else {
                            Modifier
                        }
                    ),
                state = listState,
                contentPadding = PaddingValues(bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(items = checkList) { idx, item ->
                    OnboardingCheckListItem(
                        isSelected = item.isChecked,
                        text = stringResource(item.checkListStrId),
                        onItemClick = {
                            onItemClick(idx)
                        }
                    )
                }
            }

        }
        BaseBottomBtn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            visible = isButtonVisible,
            text = stringResource(R.string.common_next),
            onClick = onNext
        )
    }
}

@Composable
fun OnboardingCheckListItem(
    isSelected: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    val (bgColor, textColor, borderColor) = if (isSelected) {
        Triple(main20, main100, main15)
    } else {
        Triple(gray700, gray500, gray500)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .border(width = 1.dp, shape = CircleShape, color = borderColor)
            .background(bgColor)
            .noRippleClickable{ onItemClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(iconId = if (isSelected) R.drawable.ic_checkbox_green else R.drawable.ic_checkbox_gray)

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = text, style = goolbitgTypography.caption2, color = textColor)
    }
}
