package com.project.presentation.buyornot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.navigation.BaseBottomNavBar
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.spacingLg
import com.project.presentation.ui.theme.spacingXs
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@Composable
@Preview(widthDp = 400, heightDp = 800)
fun BuyOrNotScreen(navHostController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent,
            bottomBar = {
                BaseBottomNavBar(navController = navHostController)
            }
        ) { innerPadding ->
            BuyOrNotContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
fun BuyOrNotContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        BuyOrNotHeader(
            onWritePosting = {

            }
        )
        BuyOrNotTopButtonContent(
            onMyPosting = {

            }
        )
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
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.buyornot_header_title),
            style = goolbitgTypography.h1,
            color = white
        )

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
fun BuyOrNotTopButtonContent(
    modifier: Modifier = Modifier,
    onMyPosting: () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .clip(RoundedCornerShape(spacingXs))
                .background(gray600)
                .noRippleClickable {
                    onMyPosting()
                }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseIcon(iconId = R.drawable.ic_docs)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.buyornot_my_write),
                style = goolbitgTypography.body5,
                color = gray100
            )
            BaseIcon(iconId = R.drawable.ic_arrow_right)
        }
        Spacer(modifier = Modifier.height(24.dp))
        BuyOrNotCardContent(modifier = Modifier.weight(1f))
        BuyOrNotGoodOrBad(modifier = Modifier.padding(top = 24.dp, bottom = 21.dp))
    }
}

@Composable
@Preview
fun BuyOrNotCardContent(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState { Int.MAX_VALUE }
    HorizontalPager(
        modifier = modifier.fillMaxWidth(),
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 30.dp)
    ) { page ->
        BuyOrNotCard()
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BuyOrNotCard(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
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
            Box(modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center){
                GlideImage(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(roundSm)),
                    model = "",
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "덱스톤", style = goolbitgTypography.body4, color = white)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "70,000원", style = goolbitgTypography.h3, color = white)
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
                Text(text = "누가 헬멧훔쳐가서 중고로 구하는 중", style = goolbitgTypography.body4, color = white)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseIcon(iconId = R.drawable.ic_bad)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "중고품이라 좀 걸림..", style = goolbitgTypography.body4, color = white)
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
