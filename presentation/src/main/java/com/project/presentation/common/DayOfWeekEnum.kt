package com.project.presentation.common

import androidx.annotation.StringRes
import com.project.presentation.R

enum class DayOfWeekEnum(@StringRes val strId: Int, @StringRes val strId2: Int) {
    Mon(strId = R.string.common_monday, strId2 = R.string.common_monday_2),
    Tue(strId = R.string.common_tuesday, strId2 = R.string.common_tuesday_2),
    Wed(strId = R.string.common_wednesday, strId2 = R.string.common_wednesday_2),
    Thu(strId = R.string.common_thursday, strId2 = R.string.common_thursday_2),
    Fri(strId = R.string.common_friday, strId2 = R.string.common_friday_2),
    Sat(strId = R.string.common_saturday, strId2 = R.string.common_saturday_2),
    Sun(strId = R.string.common_sunday, strId2 = R.string.common_sunday_2),
}