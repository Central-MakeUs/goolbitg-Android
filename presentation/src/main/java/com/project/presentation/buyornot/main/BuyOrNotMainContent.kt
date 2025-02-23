package com.project.presentation.buyornot.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.PagerState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.presentation.R
import com.project.presentation.base.BaseBottomBtn
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTintIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.base.extension.StringExtension.priceComma
import com.project.presentation.item.ReportReason
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
import com.project.presentation.ui.theme.white
import com.valentinilk.shimmer.shimmer
import kotlin.math.absoluteValue

@Composable
fun BuyOrNotCardMainContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    isLoading: Boolean,
    pageOffset: Int,
    postingList: List<BuyOrNotPostingModel>,
    onOpenReportSheet: (BuyOrNotPostingModel) -> Unit,
    onFetchNextPage: () -> Unit,
    onVote: (Int, Boolean) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (pagerState.pageCount == 0) {
            BuyOrNotCardSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 30.dp)
            )
        } else {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 30.dp)
            ) { page ->

                LaunchedEffect(page) {
                    if (!isLoading && page > pageOffset - 5) {
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
        val posting = if (postingList.isNotEmpty()) postingList[pagerState.currentPage] else null
        BuyOrNotGoodOrBad(
            modifier = Modifier.padding(top = 24.dp, bottom = 21.dp),
            item = posting,
            onVote = onVote
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BuyOrNotCard(
    modifier: Modifier = Modifier,
    posting: BuyOrNotPostingModel,
    onOpenReportSheet: (BuyOrNotPostingModel) -> Unit
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
                        onOpenReportSheet(posting)
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
                Text(
                    text = stringResource(R.string.common_price_value).replace(
                        "#VALUE#",
                        posting.productPrice.priceComma()
                    ), style = goolbitgTypography.h3, color = white
                )
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
fun BuyOrNotCardSkeleton(modifier: Modifier = Modifier) {
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
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier,
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
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.2f)),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(64.dp, 12.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(150.dp, 16.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.2f)),
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(white.copy(alpha = 0.1f))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .shimmer()
                        .background(white.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(160.dp, 12.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.1f))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .shimmer()
                        .background(white.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(160.dp, 12.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.1f))
                )
            }
        }
    }
}

@Composable
fun BuyOrNotGoodOrBad(
    modifier: Modifier = Modifier,
    item: BuyOrNotPostingModel?,
    onVote: (Int, Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseIcon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (item != null) {
                            onVote(item.id, true)
                        }
                    },
                iconId = R.drawable.ic_thumbs_up
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item?.goodVoteCount?.toString() ?: "", style = goolbitgTypography.caption2, color = gray400)
        }
        Spacer(modifier = Modifier.width(40.dp))
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseIcon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (item != null) {
                            onVote(item.id, false)
                        }
                    },
                iconId = R.drawable.ic_thumbs_down
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item?.badVoteCount?.toString() ?: "", style = goolbitgTypography.caption2, color = gray400)
        }
    }
}

@Composable
fun ReportBottomSheetContent(
    modifier: Modifier = Modifier,
    reasonList: List<ReportReason>,
    onItemClick: (Int) -> Unit,
    onReport: (String) -> Unit
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

        val reason = reasonList.findLast { it.isSelected }?.reasonEnum?.let {
            stringResource(it.strId)
        }
        BaseBottomBtn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.buyornot_report),
            enabled = reason != null,
            onClick = {
                if (reason != null) {
                    onReport(reason)
                }
            }
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
