package com.project.presentation.buyornot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.base.extension.StringExtension.priceComma
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.white
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.transparent
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun BuyOrNotCardMyContent(
    modifier: Modifier = Modifier,
    myPostingList: List<BuyOrNotPostingModel>,
    isLoading: Boolean,
    pageOffset: Int,
    onFetchNextPage: () -> Unit,
    onMyItemClick: (BuyOrNotPostingModel) -> Unit,
    onActiveDeleteMyPostingPopup: (BuyOrNotPostingModel) -> Unit,
    onModifyMyPosting: (BuyOrNotPostingModel) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            text = stringResource(R.string.buyornot_my_title),
            style = goolbitgTypography.body3,
            color = gray50
        )

        val listState = rememberLazyListState()
        LaunchedEffect(listState, pageOffset, isLoading) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .distinctUntilChanged()
                .collect { visibleItems ->
                    val lastVisibleItem = visibleItems.lastOrNull()
                    // 로드된 브랜드 목록이 있고, 현재 불러오고 있지 않은 경우에만 수행
                    if (myPostingList.isNotEmpty() && !isLoading) {
                        if (lastVisibleItem != null && lastVisibleItem.index > pageOffset - 5) {
                            onFetchNextPage()
                        }
                    }
                }
        }
        if (myPostingList.isEmpty()) {
            if (isLoading) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    repeat(3) { idx ->
                        BuyOrNotMyPostingSkeleton()
                        if (idx < myPostingList.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(gray600)
                            )
                        }
                    }
                }
            } else {
                MyPostingEmptyContent()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                state = listState
            ) {
                itemsIndexed(items = myPostingList) { idx, item ->
                    BuyOrNotMyPosting(
                        modifier = Modifier.fillMaxWidth(),
                        posting = item,
                        onMyItemClick = onMyItemClick,
                        onActiveDeleteMyPostingPopup = onActiveDeleteMyPostingPopup,
                        onModifyMyPosting = onModifyMyPosting
                    )
                    if (idx < myPostingList.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(gray600)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyPostingDropdown(
    modifier: Modifier = Modifier,
    onModify: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = gray500, shape = RoundedCornerShape(8.dp))
            .background(gray600)
    ) {
        Row(
            modifier = Modifier
                .noRippleClickable { onModify() }
                .padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseIcon(iconId = R.drawable.ic_dropdown_pencil)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.common_modify), color = gray200, style = goolbitgTypography.body3)
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(gray500)
        )
        Row(
            modifier = Modifier
                .noRippleClickable { onDelete() }
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseIcon(iconId = R.drawable.ic_dropdown_trash)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.common_delete), color = gray200, style = goolbitgTypography.body3)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BuyOrNotMyPosting(
    modifier: Modifier = Modifier,
    posting: BuyOrNotPostingModel,
    onMyItemClick: (BuyOrNotPostingModel) -> Unit,
    onActiveDeleteMyPostingPopup: (BuyOrNotPostingModel) -> Unit,
    onModifyMyPosting: (BuyOrNotPostingModel) -> Unit,
) {
    var isDropdownVisible by remember { mutableStateOf(false) }

    Row(modifier = modifier
        .fillMaxWidth()
        .noRippleClickable { onMyItemClick(posting) }
        .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        GlideImage(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp)),
            model = posting.productImageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .height(80.dp)
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = posting.productName,
                style = goolbitgTypography.body3,
                color = white,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.common_price_value).replace(
                    "#VALUE#",
                    posting.productPrice.priceComma()
                ),
                style = goolbitgTypography.body4,
                color = gray200
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                BaseIcon(iconId = R.drawable.ic_thumb_up_small)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = posting.goodVoteCount.toString(), style = goolbitgTypography.body5, color = main100)
                Spacer(modifier = Modifier.width(8.dp))
                BaseIcon(iconId = R.drawable.ic_thumb_down_small)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = posting.badVoteCount.toString(), style = goolbitgTypography.body5, color = gray300)
            }
        }
        Box {
            BaseIcon(
                modifier = Modifier
                    .noRippleClickable {
                        isDropdownVisible = true
                    },
                iconId = R.drawable.ic_vertical_menu_dots
            )
            DropdownMenu(
                expanded = isDropdownVisible,
                containerColor = transparent,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                onDismissRequest = { isDropdownVisible = false }
            ) {
                MyPostingDropdown(
                    onModify = {
                        isDropdownVisible = false
                        onModifyMyPosting(posting)
                    },
                    onDelete = {
                        isDropdownVisible = false
                        onActiveDeleteMyPostingPopup(posting)
                    }
                )
            }
        }
    }
}

@Composable
fun BuyOrNotMyPostingSkeleton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
                .background(white.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .height(80.dp)
                .padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(121.dp, 16.dp)
                    .clip(RoundedCornerShape(roundSm))
                    .shimmer()
                    .background(white.copy(alpha = 0.1f))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(48.dp, 12.dp)
                    .clip(RoundedCornerShape(roundSm))
                    .shimmer()
                    .background(white.copy(alpha = 0.1f))
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp, 12.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp, 12.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.1f))
                )
            }
        }
        Box {
            BaseIcon(
                modifier = Modifier,
                iconId = R.drawable.ic_vertical_menu_dots
            )
        }
    }
}


@Composable
fun MyPostingEmptyContent(modifier: Modifier = Modifier) {

}
