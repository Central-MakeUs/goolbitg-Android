package com.project.presentation.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.project.presentation.R

private val PretendFontFamily = FontFamily(
    Font(R.font.pretendard_bold, weight = FontWeight.Bold),
    Font(R.font.pretendard_semibold, weight = FontWeight.SemiBold),
    Font(R.font.pretendard_medium, weight = FontWeight.Medium),
    Font(R.font.pretendard_regular, weight = FontWeight.Normal),
)

val goolbitgTypography = GoolbitgTypography(
    h1 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    h2 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    h3 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    body1 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 19.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    body2 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    body3 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    body4 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    caption1 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    caption2 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    btn1 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    btn2 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    ),
    btn3 = TextStyle(
        fontFamily = PretendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
)

@Immutable
data class GoolbitgTypography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val body4: TextStyle,
    val caption1: TextStyle,
    val caption2: TextStyle,
    val btn1: TextStyle,
    val btn2: TextStyle,
    val btn3: TextStyle,
)