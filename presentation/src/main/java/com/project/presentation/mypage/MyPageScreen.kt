package com.project.presentation.mypage

import android.content.Intent
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.data.remote.common.BaseUrl.CS_URL
import com.project.data.remote.common.BaseUrl.PRIVACY_AND_POLICY_URL
import com.project.data.remote.common.BaseUrl.TERMS_OF_SERVICES_URL
import com.project.domain.model.user.UserInfoModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.fadingEdge
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.base.extension.StringExtension.priceComma
import com.project.presentation.item.MyPageUsageGuideEnum
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.roundMd
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@Composable
@Preview
fun MyPageScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    var isLogout by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(state.value.isLogoutSuccess) {
        if(state.value.isLogoutSuccess){
            navHostController.navigate(NavItem.Login.route) {
                popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.img_mypage_bg),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
        Scaffold(containerColor = transparent,
            bottomBar = {
                BaseBottomNavBar(navController = navHostController)
            }) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                MyPageHeader(
                    newAlarmCount = 1,
                    onNotification = {
                        // TODO: 알림 목록 화면 이동
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
                MyPageContent(
                    userInfoModel = state.value.userInfoModel,
                    onUsageGuideClick = { item ->
                        when(item){
                            MyPageUsageGuideEnum.TermsAndServices -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_OF_SERVICES_URL))
                                context.startActivity(intent)
                            }
                            MyPageUsageGuideEnum.PrivacyAndPolicy -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_AND_POLICY_URL))
                                context.startActivity(intent)
                            }
                            MyPageUsageGuideEnum.Cs -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CS_URL))
                                context.startActivity(intent)
                            }
                            else -> Unit
                        }
                    },
                    onLogout = {
                        isLogout = true
                    },
                    onWithdraw = {

                    }
                )
            }
        }

        if (isLogout) {
            MyPageLogoutPopup(
                onConfirm = {
                    isLogout = false
                    viewModel.onEvent(MyPageEvent.Logout)
                },
                onDismiss = {
                    isLogout = false
                }
            )
        }
    }
}

@Composable
fun MyPageHeader(
    newAlarmCount: Int = 0,
    modifier: Modifier = Modifier,
    onNotification: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.mypage_header_title),
            style = goolbitgTypography.h1,
            color = white
        )

        Box(modifier = Modifier
            .size(31.dp, 30.dp)
            .noRippleClickable { onNotification() }) {
            BaseIcon(
                modifier = Modifier.align(Alignment.BottomStart),
                iconId = R.drawable.ic_notification
            )
            if (newAlarmCount != 0) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(com.project.presentation.ui.theme.error)
                        .align(Alignment.TopEnd)
                ) {

                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = newAlarmCount.toString(),
                        style = goolbitgTypography.body5,
                        color = white,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MyPageContent(
    modifier: Modifier = Modifier,
    userInfoModel: UserInfoModel?,
    onUsageGuideClick: (MyPageUsageGuideEnum) -> Unit,
    onLogout: () -> Unit,
    onWithdraw: () -> Unit
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
                    0.03f to white,
                    0.97f to white,
                    1f to transparent
                )

                showTopFade -> Brush.verticalGradient(0f to transparent, 0.03f to white)
                showBottomFade -> Brush.verticalGradient(0.97f to white, 1f to transparent)
                else -> null
            }
        }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .then(
                if (listFade != null) {
                    Modifier.fadingEdge(listFade!!)
                } else {
                    Modifier
                }
            ),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MyPageInfoCard(
                userInfoModel = userInfoModel
            )
        }
//        item {
//            MyPageConsumeHabit()
//        }
        item {
            MyPageAccountManagement(accountId = userInfoModel?.id ?: "")
        }
        item {
            MyPageUsageGuide(
                onUsageGuideClick = onUsageGuideClick
            )
        }
        item {
            MyPageLogoutAndWithdraw(
                onLogout = onLogout,
                onWithdraw = onWithdraw
            )
            Spacer(modifier = Modifier.height(19.dp))
        }
    }
}

@Composable
fun MyPageInfoCard(
    modifier: Modifier = Modifier,
    userInfoModel: UserInfoModel?
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(roundSm))
            .background(
                brush = Brush.horizontalGradient(listOf(main100, Color(0xFF67BF4E)))
            )
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BaseIcon(modifier = Modifier.size(54.dp), iconId = R.drawable.ic_mypage_profile_default)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userInfoModel?.spendingType?.title ?: "",
                    style = goolbitgTypography.h4,
                    color = white.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.mypage_card_nickname).replace(
                        "#VALUE#",
                        userInfoModel?.nickname ?: ""
                    ),
                    style = goolbitgTypography.h1,
                    color = white
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.mypage_card_consume_score),
                    style = goolbitgTypography.body5,
                    color = white
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.mypage_card_consume_score_value).replace(
                        "#VALUE#",
                        userInfoModel?.spendingHabitScore?.toString() ?: "00"
                    ),
                    style = goolbitgTypography.h3,
                    color = white
                )
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 13.dp, vertical = 2.dp)
                    .height(40.dp)
                    .width(1.dp)
                    .background(white.copy(alpha = 0.2f))
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.mypage_card_total_challenge_count),
                    style = goolbitgTypography.body5,
                    color = white
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userInfoModel?.challengeCount?.toString() ?: "00",
                    style = goolbitgTypography.h3,
                    color = white
                )
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 13.dp, vertical = 2.dp)
                    .height(40.dp)
                    .width(1.dp)
                    .background(white.copy(alpha = 0.2f))
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.mypage_card_posting_count),
                    style = goolbitgTypography.body5,
                    color = white
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userInfoModel?.postCount?.toString() ?: "00",
                    style = goolbitgTypography.h3,
                    color = white
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        userInfoModel?.spendingType?.goal?.let {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.mypage_card_level_title).replace(
                        "#VALUE#",
                        (it - userInfoModel.achievementGuage).priceComma()
                    ), style = goolbitgTypography.body4, color = white
                )
                Spacer(modifier = Modifier.height(4.dp))
                val progress = userInfoModel.achievementGuage * 100 / it
                MyPageExpProgress(progress = progress)
            }
        }
    }
}

@Composable
fun MyPageExpProgress(
    progress: Int,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(17.dp)
            .innerShadow(
                color = black.copy(alpha = 0.4f),
                cornersRadius = 24.dp,
                blur = 4.dp,
                offsetX = 0.dp,
                offsetY = 2.dp
            )
    ) {
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(main100, Color(0xFF67BF4E))),
            colorFilter = ColorFilter.tint(black.copy(alpha = 0.1f)),
            cornerRadius = CornerRadius(24f, 24f)
        )
        drawRoundRect(
            color = white,
            size = Size(size.width * progress / 100, size.height),
            cornerRadius = CornerRadius(24f, 24f)
        )
    }
}

fun Modifier.innerShadow(
    color: Color = Color.Black,
    cornersRadius: Dp = 0.dp,
    spread: Dp = 0.dp,
    blur: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
): Modifier = drawWithContent {

    drawContent()

    val rect = Rect(Offset.Zero, size)
    val paint = Paint()

    drawIntoCanvas {

        paint.color = color
        paint.isAntiAlias = true
        it.saveLayer(rect, paint)
        it.drawRoundRect(
            left = rect.left,
            top = rect.top,
            right = rect.right,
            bottom = rect.bottom,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        if (blur.toPx() > 0) {
            frameworkPaint.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        }
        val left = if (offsetX > 0.dp) {
            rect.left + offsetX.toPx()
        } else {
            rect.left
        }
        val top = if (offsetY > 0.dp) {
            rect.top + offsetY.toPx()
        } else {
            rect.top
        }
        val right = if (offsetX < 0.dp) {
            rect.right + offsetX.toPx()
        } else {
            rect.right
        }
        val bottom = if (offsetY < 0.dp) {
            rect.bottom + offsetY.toPx()
        } else {
            rect.bottom
        }
        paint.color = Color.Black
        it.drawRoundRect(
            left = left + spread.toPx() / 2,
            top = top + spread.toPx() / 2,
            right = right - spread.toPx() / 2,
            bottom = bottom - spread.toPx() / 2,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
        frameworkPaint.xfermode = null
        frameworkPaint.maskFilter = null
    }
}

@Composable
fun MyPageConsumeHabit(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(roundSm))
            .border(
                width = 1.dp,
                color = white.copy(alpha = 0.2f),
                shape = RoundedCornerShape(roundSm)
            )
            .background(white.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.mypage_consume_habit),
            style = goolbitgTypography.body3,
            color = gray300
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseIcon(iconId = R.drawable.ic_chart_arrow)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.mypage_consume_habit_pattern_analysis),
                style = goolbitgTypography.caption1,
                color = white
            )
        }

    }
}

@Composable
fun MyPageAccountManagement(
    accountId: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(roundSm))
            .border(
                width = 1.dp,
                color = white.copy(alpha = 0.2f),
                shape = RoundedCornerShape(roundSm)
            )
            .background(white.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.mypage_account_management_id),
            style = goolbitgTypography.body3,
            color = gray300
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseIcon(iconId = R.drawable.ic_user)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.mypage_account_management_id),
                style = goolbitgTypography.caption1,
                color = white
            )
            Text(text = accountId, color = gray300, style = goolbitgTypography.caption1)
        }

    }
}

@Composable
fun MyPageUsageGuide(
    modifier: Modifier = Modifier,
    onUsageGuideClick: (MyPageUsageGuideEnum) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(roundSm))
            .border(
                width = 1.dp,
                color = white.copy(alpha = 0.2f),
                shape = RoundedCornerShape(roundSm)
            )
            .background(white.copy(alpha = 0.1f))
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.mypage_usage_guide),
            style = goolbitgTypography.body3,
            color = gray300
        )
        val usageList = MyPageUsageGuideEnum.entries
        usageList.forEachIndexed { idx, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { onUsageGuideClick(item) }
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseIcon(iconId = item.drawableId)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(item.strId),
                    color = white,
                    style = goolbitgTypography.caption1
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (item == MyPageUsageGuideEnum.AppVersion) {
                    val packageInfo = LocalContext.current.packageManager.getPackageInfo(
                        "com.project.goolbitg",
                        0
                    )
                    val versionCode =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode
                    Text(
                        text = "${packageInfo.versionName}($versionCode)",
                        color = gray300,
                        style = goolbitgTypography.caption1
                    )
                } else {
                    BaseIcon(iconId = R.drawable.ic_arrow_right_16)
                }
            }
            if (idx != usageList.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(gray400)
                )
            }
        }

    }
}

@Composable
fun MyPageLogoutAndWithdraw(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onWithdraw: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.noRippleClickable { onLogout() },
            text = stringResource(R.string.mypage_usage_logout),
            color = gray300,
            style = goolbitgTypography.btn3
        )
        Text(text = "・", color = gray300, style = goolbitgTypography.btn3)
        Text(
            modifier = Modifier.noRippleClickable { onWithdraw() },
            text = stringResource(R.string.mypage_usage_withdraw),
            color = gray300,
            style = goolbitgTypography.btn3
        )
    }
}

@Composable
fun MyPageLogoutPopup(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(black.copy(alpha = 0.3f))
            .noRippleClickable {  }
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(roundMd))
                .border(
                    width = 1.dp,
                    color = gray400,
                    shape = RoundedCornerShape(roundMd)
                )
                .background(gray500)
                .padding(16.dp)
                .align(Alignment.Center),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.mypage_usage_logout_popup_title),
                color = white,
                textAlign = TextAlign.Center,
                style = goolbitgTypography.h3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.mypage_usage_logout_popup_sub_title),
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                style = goolbitgTypography.caption2
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(gray400)
                        .noRippleClickable {
                            onDismiss()
                        }
                        .padding(10.5.dp),
                    text = stringResource(R.string.common_cancel),
                    color = gray100,
                    style = goolbitgTypography.btn2,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(com.project.presentation.ui.theme.error)
                        .noRippleClickable {
                            onConfirm()
                        }
                        .padding(10.5.dp),
                    text = stringResource(R.string.mypage_usage_logout),
                    color = white,
                    style = goolbitgTypography.btn2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
