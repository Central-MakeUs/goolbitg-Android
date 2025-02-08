package com.project.presentation.common

import androidx.annotation.StringRes
import com.project.data.remote.common.BaseUrl.PRIVACY_AND_POLICY_URL
import com.project.data.remote.common.BaseUrl.TERMS_OF_SERVICES_URL
import com.project.presentation.R

enum class AgreementEnum(@StringRes val strId: Int, val webUrl: String? = null) {
    AgeFourteen(strId = R.string.onboarding_first_agreement_1),
    TermsOfServices(strId = R.string.onboarding_first_agreement_2, webUrl = TERMS_OF_SERVICES_URL),
    PrivacyAndPolicy(strId = R.string.onboarding_first_agreement_3, webUrl = PRIVACY_AND_POLICY_URL),
    Advertisement(strId = R.string.onboarding_first_agreement_4)
}
