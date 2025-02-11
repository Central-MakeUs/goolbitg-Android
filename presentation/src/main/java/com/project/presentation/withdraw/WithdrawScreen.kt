package com.project.presentation.withdraw

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseHeader
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseLoadingBox
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.item.WithdrawEnum
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.main15
import com.project.presentation.ui.theme.main20
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@Composable
fun WithdrawScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: WithdrawViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.isWithdrawSuccess) {
        if (state.value.isWithdrawSuccess) {
            navHostController.navigate(NavItem.Login.route) {
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
            WithdrawContent(
                modifier = Modifier.padding(innerPadding),
                onBackPressed = {
                    navHostController.popBackStack()
                },
                onWithdraw = { reason ->
                    viewModel.withdrawAccount(reason = reason)
                }
            )
        }

        if (state.value.isLoading) {
            BaseLoadingBox()
        }
    }
}

@Composable
fun WithdrawContent(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onWithdraw: (String) -> Unit
) {
    val reasonList = WithdrawEnum.entries
    val reasonStrList = WithdrawEnum.entries.map { stringResource(it.strId) }
    var selectedReason by remember { mutableStateOf<WithdrawEnum?>(null) }
    var etcStr by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        BaseHeader(
            title = stringResource(R.string.withdraw_header_title),
            onBackPressed = onBackPressed
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.withdraw_title),
                color = white,
                style = goolbitgTypography.h1
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.withdraw_sub_title),
                color = gray300,
                style = goolbitgTypography.body1
            )
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(items = reasonList) { idx, item ->
                    ReasonItem(modifier = Modifier.fillMaxWidth(),
                        item = item,
                        isSelected = selectedReason == item,
                        onItemClick = {
                            selectedReason = it
                        }
                    )
                    if (selectedReason == WithdrawEnum.Etc && idx == reasonStrList.lastIndex) {
                        Spacer(modifier = Modifier.height(16.dp))
                        EtcTextFieldItem(
                            modifier = Modifier.fillMaxWidth(),
                            value = etcStr,
                            onValueChange = { etcStr = it })
                    }
                }
            }
        }
        val isVisible by remember(selectedReason) {
            derivedStateOf{ selectedReason != null }
        }
        val context = LocalContext.current
        BaseBottomBtn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            visible = isVisible,
            text = stringResource(R.string.common_withdraw),
            onClick = {
                if (selectedReason != null) {
                    if (selectedReason != WithdrawEnum.Etc) {
                        onWithdraw(context.getString(selectedReason!!.strId))
                    } else {
                        onWithdraw(etcStr)
                    }
                }
            }
        )
    }
}

@Composable
fun ReasonItem(
    modifier: Modifier = Modifier,
    item: WithdrawEnum,
    isSelected: Boolean,
    onItemClick: (WithdrawEnum) -> Unit
) {
    val (bgColor, textColor, borderColor) = if (isSelected) {
        Triple(main20, main100, main15)
    } else {
        Triple(gray700, gray500, gray500)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .border(width = 1.dp, shape = CircleShape, color = borderColor)
            .background(bgColor)
            .noRippleClickable { onItemClick(item) }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(iconId = if (isSelected) R.drawable.ic_checkbox_green else R.drawable.ic_checkbox_gray)

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(item.strId),
            style = goolbitgTypography.caption2,
            color = textColor
        )
    }
}

@Composable
fun EtcTextFieldItem(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .height(48.dp)
            .clip(CircleShape)
            .border(width = 1.dp, shape = CircleShape, color = gray500)
            .background(gray600)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (value.isEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.withdraw_placeholder),
                overflow = TextOverflow.Ellipsis,
                color = gray400,
                style = goolbitgTypography.caption2
            )
        }
        BasicTextField(
            modifier = Modifier.fillMaxWidth().background(transparent),
            value = value,
            onValueChange = onValueChange,
            textStyle = goolbitgTypography.caption2.copy(color = white),
            maxLines = 1,
            singleLine = true,
            cursorBrush = SolidColor(white)
        )
    }
}