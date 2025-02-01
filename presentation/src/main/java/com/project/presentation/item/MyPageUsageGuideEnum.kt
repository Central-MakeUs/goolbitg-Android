package com.project.presentation.item

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.project.presentation.R

enum class MyPageUsageGuideEnum(@DrawableRes val drawableId: Int, @StringRes val strId: Int) {
    AppVersion(drawableId = R.drawable.ic_box, strId = R.string.mypage_usage_guide_app_version),
    Cs(drawableId = R.drawable.ic_headphones, strId = R.string.mypage_usage_guide_cs),
//    Notice(drawableId = R.drawable.ic_megaphone, strId = R.string.mypage_usage_guide_notice),
//    Qna(drawableId = R.drawable.ic_interrogation, strId = R.string.mypage_usage_guide_qna),
    TermsAndServices(drawableId = R.drawable.ic_document, strId = R.string.mypage_usage_guide_terms_and_services),
    PrivacyAndPolicy(drawableId = R.drawable.ic_document, strId = R.string.mypage_usage_guide_privacy_and_policy),
//    Advertisement(drawableId = R.drawable.ic_document, strId = R.string.mypage_usage_guide_advertisement),
//    Teenager(drawableId = R.drawable.ic_document, strId = R.string.mypage_usage_guide_teenager),
//    License(drawableId = R.drawable.ic_document, strId = R.string.mypage_usage_guide_opensource_license),
}
