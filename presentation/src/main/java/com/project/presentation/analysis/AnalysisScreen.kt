package com.project.presentation.analysis

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.project.domain.model.analysis.AnalysisReportModel
import com.project.domain.model.analysis.AnalysisSummary
import com.project.domain.model.analysis.BuyOrNotAnalysis
import com.project.domain.model.analysis.CategoryAnalysis
import com.project.domain.model.analysis.CategoryScore
import com.project.domain.model.analysis.CompletionAnalysis
import com.project.domain.model.analysis.IndvGroupAnalysis
import com.project.presentation.R
import com.project.presentation.base.BaseHeader
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.main80
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val ChartGreenLight = Color(0xFF67BF4E)
private val ChartGreenDark = Color(0xFF2F8F18)

// ─── Stateful ────────────────────────────────────────────────────────────────
@Composable
fun AnalysisScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = bg1,
        topBar = {
            BaseHeader(
                title = "소비습관 분석",
                onBackPressed = { navHostController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val report = state.report
            if (report != null) {
                AnalysisContent(report = report)
            } else if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = main100)
                }
            }
        }
    }
}

// ─── Stateless ───────────────────────────────────────────────────────────────
@Composable
fun AnalysisContent(
    report: AnalysisReportModel,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SummaryHeader(report.summary) }
        item {
            Text(
                text = "더 다양한 소비 습관을 분석해봤어요!",
                style = goolbitgTypography.h4,
                color = white,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 0.dp)
            )
        }
        item { CompletionCard(report.completionAnalysis) }
        item { CategoryCard(report.categoryAnalysis) }
        item { IndvGroupCard(report.indvGroupAnalysis) }
        item { BuyOrNotCard(report.buyOrNotAnalysis) }
        // 콘텐츠 끝의 "맨 위로 올라가기" — 스크롤 끝에 도달했을 때만 노출되는 자연스러운 위치
        item {
            ScrollToTopButton(
                onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } }
            )
        }
    }
}

@Composable
private fun ScrollToTopButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        BaseIcon(modifier = Modifier.size(32.dp), iconId = R.drawable.ic_arrow_up)
        Text(
            text = "맨 위로 올라가기",
            style = goolbitgTypography.body5,
            color = gray200
        )
    }
}

// ─── 공통 카드 wrapper ───────────────────────────────────────────────────────
@Composable
private fun AnalysisCard(
    label: String,
    message: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(gray700)
            .padding(20.dp)
    ) {
        Text(text = label, style = goolbitgTypography.body5, color = gray300)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = goolbitgTypography.h4,
            color = white,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        content()
    }
}

// ─── 0. 사용자 요약 — 카드가 아닌 bg1 배경에 직접 노출 ──────────────────────
@Composable
private fun SummaryHeader(data: AnalysisSummary) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1행: "username님은 spendingType 중"
            Text(
                text = "${data.username}님은 ${data.spendingType} 중",
                style = goolbitgTypography.body2,
                color = white
            )
            Spacer(modifier = Modifier.height(4.dp))
            // 2행: "상위 {percentage}%" — 수치는 main100 강조
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = white, fontWeight = FontWeight.Bold)) {
                        append("상위 ")
                    }
                    withStyle(SpanStyle(color = main100, fontWeight = FontWeight.Bold)) {
                        append("${data.percentage}%")
                    }
                },
                style = goolbitgTypography.h2,
                fontWeight = FontWeight.Bold
            )
        }
        Box(modifier = Modifier.size(200.dp).align(Alignment.TopEnd)) {
            if (data.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = data.imageUrl,
                    contentDescription = data.spendingType,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(200.dp)
                )
            } else {
                Box(modifier = Modifier.size(200.dp))
            }
        }
    }
}

// ─── 1. 최근 챌린지 비교 ─────────────────────────────────────────────────────
@Composable
private fun CompletionCard(data: CompletionAnalysis) {
    AnalysisCard(label = "최근 챌린지 비교", message = data.message) {
        val maxValue = maxOf(data.prev, data.current, data.recommendation, 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            CompletionBar(
                value = data.prev,
                maxValue = maxValue,
                label = "지난주",
                style = BarStyle.GRAY
            )
            CompletionBar(
                value = data.current,
                maxValue = maxValue,
                label = "이번주",
                style = BarStyle.GREEN
            )
            CompletionBar(
                value = data.recommendation,
                maxValue = maxValue,
                label = "다음주",
                style = BarStyle.OUTLINE,
                badge = "추천"
            )
        }
    }
}

private enum class BarStyle { GRAY, GREEN, OUTLINE }

@Composable
private fun CompletionBar(
    value: Int,
    maxValue: Int,
    label: String,
    style: BarStyle,
    badge: String? = null
) {
    val ratio = (value.toFloat() / maxValue.coerceAtLeast(1)).coerceIn(0.15f, 1f)
    val barHeight = 160.dp * ratio
    val barShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 추천 막대는 막대 위에 "추천 / {N}개" 두 줄 (모두 main100)
        if (style == BarStyle.OUTLINE) {
            Text(
                text = badge ?: "추천",
                style = goolbitgTypography.body5,
                color = main100
            )
            Text(
                text = "${value}개",
                style = goolbitgTypography.body4,
                color = main100,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            // 일반 막대: 한 줄 빈 라인 + 값
            Text(text = " ", style = goolbitgTypography.body5)
            Text(
                text = "${value}개",
                style = goolbitgTypography.body4,
                color = if (style == BarStyle.GRAY) gray300 else white,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(barHeight)
                .then(
                    when (style) {
                        // 회색 막대: gray500 → bg1 페이드
                        BarStyle.GRAY -> Modifier
                            .clip(barShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to gray500,
                                        1f to gray700
                                    )
                                )
                            )
                        // 그린 막대: ChartGreenLight → main100 → bg1
                        BarStyle.GREEN -> Modifier
                            .clip(barShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to main100,
                                        1f to gray700
                                    )
                                )
                            )
                        // 다음주: dashed outline + fill 모두 하단 bg1 페이드
                        BarStyle.OUTLINE -> Modifier
                            .clip(barShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to gray700,
                                        1f to gray700
                                    )
                                )
                            )
                            .dashedBorder(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to main100,
                                        1f to bg1
                                    )
                                ),
                                shape = barShape,
                                dashLength = 6.dp,
                                gapLength = 6.dp
                            )
                    }
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = goolbitgTypography.body5,
            color = gray300
        )
    }
}

/** 점선 테두리 Modifier — Compose 표준에 없어 PathEffect 로 직접 구현. brush 로 stroke 색에 그라디언트 적용 가능 */
private fun Modifier.dashedBorder(
    width: Dp,
    brush: Brush,
    shape: Shape,
    dashLength: Dp,
    gapLength: Dp
): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    val stroke = Stroke(
        width = width.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            0f
        )
    )
    drawOutline(outline = outline, brush = brush, style = stroke)
}

// ─── 2. 카테고리별 비교 (레이더 차트) ────────────────────────────────────────
@Composable
private fun CategoryCard(data: CategoryAnalysis) {
    // 성공한 카테고리(success >= total, total > 0) 가 2개 이상이면 클라이언트에서 메시지 override
    val successCount = data.scores.count { it.total > 0 && it.success >= it.total }
    val message = if (successCount >= 2) {
        "${successCount}개의 카테고리를 모두 성공했어요!"
    } else {
        data.message
    }
    AnalysisCard(label = "카테고리별 비교", message = message) {
        RadarChart(scores = data.scores)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun RadarChart(scores: List<CategoryScore>) {
    if (scores.isEmpty()) return
    val n = scores.size
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        // 라벨 영역 확보 위해 차트 반지름은 짧은 변의 35% 정도
        val radiusPx = (minOf(widthPx, heightPx) * 0.35f)
        val centerX = widthPx / 2f
        val centerY = heightPx / 2f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val cornerPx = 4.dp.toPx()
            val cornerEffect = PathEffect.cornerPathEffect(cornerPx)
            val androidCornerEffect = android.graphics.CornerPathEffect(cornerPx)

            // 가장 큰 (level 5) 펜타곤 fill = gray600, 모서리 8dp 라운드
            val outerPath = Path()
            for (i in 0 until n) {
                val angle = -PI / 2 + 2 * PI * i / n
                val x = centerX + (radiusPx * cos(angle)).toFloat()
                val y = centerY + (radiusPx * sin(angle)).toFloat()
                if (i == 0) outerPath.moveTo(x, y) else outerPath.lineTo(x, y)
            }
            outerPath.close()
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = gray600.toArgb()
                    style = android.graphics.Paint.Style.FILL
                    pathEffect = androidCornerEffect
                }
                canvas.nativeCanvas.drawPath(outerPath.asAndroidPath(), paint)
            }

            // 가이드 5단계 동심 다각형 라인 — gray500, 모서리 8dp 라운드
            for (level in 1..5) {
                val r = radiusPx * level / 5f
                val path = Path()
                for (i in 0 until n) {
                    val angle = -PI / 2 + 2 * PI * i / n
                    val x = centerX + (r * cos(angle)).toFloat()
                    val y = centerY + (r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(
                    path = path,
                    color = gray500,
                    style = Stroke(width = 1.dp.toPx(), pathEffect = cornerEffect)
                )
            }

            // 데이터 다각형 (성공률) — fill main100 0.35α + stroke main100 2dp, 모서리 라운드
            val dataPath = Path()
            var hasAny = false
            for ((i, score) in scores.withIndex()) {
                val ratio = if (score.total > 0) score.success.toFloat() / score.total else 0f
                if (ratio > 0f) hasAny = true
                val r = radiusPx * ratio
                val angle = -PI / 2 + 2 * PI * i / n
                val x = centerX + (r * cos(angle)).toFloat()
                val y = centerY + (r * sin(angle)).toFloat()
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()
            if (hasAny) {
                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        isAntiAlias = true
                        color = main80.toArgb()
                        style = android.graphics.Paint.Style.FILL
                        pathEffect = androidCornerEffect
                    }
                    canvas.nativeCanvas.drawPath(dataPath.asAndroidPath(), paint)
                }
                drawPath(
                    path = dataPath,
                    color = Color.White.copy(alpha = 0.3f),
                    style = Stroke(width = 1.dp.toPx(), pathEffect = cornerEffect)
                )
            }
        }

        // 카테고리 라벨 — 각 라벨의 실제 측정 크기로 정확히 중앙 정렬
        val labelDistancePx = radiusPx + with(density) { 28.dp.toPx() }
        scores.forEachIndexed { i, score ->
            val angle = -PI / 2 + 2 * PI * i / n
            val anchorX = centerX + (labelDistancePx * cos(angle)).toFloat()
            val anchorY = centerY + (labelDistancePx * sin(angle)).toFloat()
            var labelSize by remember { mutableStateOf(IntSize.Zero) }
            Column(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (anchorX - labelSize.width / 2f).roundToInt(),
                            (anchorY - labelSize.height / 2f).roundToInt()
                        )
                    }
                    .onGloballyPositioned { labelSize = it.size },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = score.catName,
                    style = goolbitgTypography.body5,
                    color = gray200
                )
                // success >= total 이면 success 숫자 main100 강조, 단 0/0 케이스는 white
                val successColor = if (score.total > 0 && score.success >= score.total) main100 else white
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = successColor)) {
                            append(score.success.toString())
                        }
                        withStyle(SpanStyle(color = gray400)) {
                            append("/${score.total}")
                        }
                    },
                    style = goolbitgTypography.body5
                )
            }
        }
    }
}

// ─── 3. 그룹 챌린지 비교 ─────────────────────────────────────────────────────
@Composable
private fun IndvGroupCard(data: IndvGroupAnalysis) {
    AnalysisCard(label = "그룹 챌린지 비교", message = data.message) {
        val maxValue = maxOf(data.indvScore, data.groupScore, 0.01f)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            IndvGroupBar(
                ratio = data.indvScore / maxValue,
                label = "개인",
                style = BarStyle.GRAY,
                people = 1
            )
            IndvGroupBar(
                ratio = data.groupScore / maxValue,
                label = "그룹",
                style = BarStyle.GREEN,
                people = 3
            )
        }
    }
}

@Composable
private fun IndvGroupBar(
    ratio: Float,
    label: String,
    style: BarStyle,
    people: Int
) {
    val barMaxHeight = 140.dp
    val barHeight = (barMaxHeight * ratio.coerceIn(0.2f, 1f))
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(barHeight)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .then(
                    when (style) {
                        BarStyle.GRAY -> Modifier.background(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to gray500,
                                    1f to gray700
                                )
                            )
                        )
                        BarStyle.GREEN -> Modifier.background(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to main100,
                                    1f to gray700
                                )
                            )
                        )
                        else -> Modifier
                    }
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = goolbitgTypography.body4, color = gray200)
        Spacer(modifier = Modifier.height(8.dp))
        if (people > 1) {
            BaseIcon(iconId = R.drawable.ic_user_group)
        } else {
            BaseIcon(iconId = R.drawable.ic_user_individual)
        }
    }
}

// ─── 4. 살까말까 투표 결과 (도넛) ────────────────────────────────────────────
@Composable
private fun BuyOrNotCard(data: BuyOrNotAnalysis) {
    // 점수 → 비율(%) 로 변환. 두 값의 합을 100% 로 잡고 buy 비율부터 계산, 나머지를 not 비율로
    val total = (data.buyScore + data.notScore).coerceAtLeast(1)
    val buyPercent = (data.buyScore * 100f / total).roundToInt()
    val notPercent = 100 - buyPercent

    // 더 높은 비율 쪽만 main100, 낮은 쪽은 gray300, 동률(50:50)이면 둘 다 main100
    val buyColor = if (buyPercent >= notPercent) main100 else gray300
    val notColor = if (notPercent >= buyPercent) main100 else gray300

    AnalysisCard(label = "살까말까 투표 결과", message = data.message) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            VoteIconLabel(
                iconId = R.drawable.ic_thumbs_up,
                value = buyPercent,
                color = buyColor
            )
            BuyOrNotDonut(
                buy = data.buyScore,
                not = data.notScore
            )
            VoteIconLabel(
                iconId = R.drawable.ic_thumbs_down,
                value = notPercent,
                color = notColor
            )
        }
    }
}

@Composable
private fun VoteIconLabel(iconId: Int, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BaseIcon(
            modifier = Modifier.size(48.dp),
            iconId = iconId
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$value%",
            style = goolbitgTypography.body4,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BuyOrNotDonut(buy: Int, not: Int) {
    val total = (buy + not).coerceAtLeast(1)
    val buyRatio = buy.toFloat() / total
    val notRatio = 1f - buyRatio
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 한 원을 비율대로 좌(buy) / 우(not) 분할.
            // 6시(90°) 를 기준선으로 두고 → buy 는 시계반대(좌측), not 은 시계방향(우측)으로 자람.
            // 50:50 → 좌·우 정확히 반반, 100:0 → 한 색이 전체 원, 0:100 → 그 반대.
            val s = size.minDimension
            val arcSize = Size(s, s)
            val topLeft = Offset((size.width - s) / 2, (size.height - s) / 2)
            val buySweep = 360f * buyRatio
            val notSweep = 360f * notRatio
            // 좋아요(buy) — 6시에서 좌측(반시계)으로 buySweep 만큼
            drawArc(
                color = ChartGreenLight,
                startAngle = 90f - buySweep,
                sweepAngle = buySweep,
                useCenter = true,
                topLeft = topLeft,
                size = arcSize
            )
            // 싫어요(not) — 6시에서 우측(시계방향)으로 notSweep 만큼
            drawArc(
                color = ChartGreenDark,
                startAngle = 90f,
                sweepAngle = notSweep,
                useCenter = true,
                topLeft = topLeft,
                size = arcSize
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(widthDp = 400, heightDp = 1400)
@Composable
private fun AnalysisContentPreview() {
    AnalysisContent(
        report = AnalysisReportModel(
            summary = AnalysisSummary(
                username = "굴비",
                imageUrl = "",
                percentage = 80,
                spendingType = "절약형"
            ),
            completionAnalysis = CompletionAnalysis(
                message = "지금 이대로 유지해도 좋아요!",
                prev = 7,
                current = 10,
                recommendation = 10
            ),
            categoryAnalysis = CategoryAnalysis(
                message = "아직 달성한 카테고리가 없어요!",
                scores = listOf(
                    CategoryScore("식비", 6, 0),
                    CategoryScore("기타", 4, 0),
                    CategoryScore("생활비", 7, 0),
                    CategoryScore("쇼핑", 4, 0),
                    CategoryScore("교통비", 7, 0)
                )
            ),
            indvGroupAnalysis = IndvGroupAnalysis(
                message = "{혼자할} 때 성공률이 {40%} 높아요!",
                indvScore = 0.7f,
                groupScore = 1.0f
            ),
            buyOrNotAnalysis = BuyOrNotAnalysis(
                message = "의견이 반반이에요!",
                buyScore = 50,
                notScore = 50
            )
        )
    )
}
