package com.project.presentation.common

import androidx.annotation.StringRes
import com.project.presentation.R

enum class DayOfWeekEnum(@StringRes val strId: Int, @StringRes val strId2: Int, val value: String) {
    Mon(strId = R.string.common_monday, strId2 = R.string.common_monday_2, value = "MONDAY"),
    Tue(strId = R.string.common_tuesday, strId2 = R.string.common_tuesday_2, value = "TUESDAY"),
    Wed(strId = R.string.common_wednesday, strId2 = R.string.common_wednesday_2, value = "WEDNESDAY"),
    Thu(strId = R.string.common_thursday, strId2 = R.string.common_thursday_2, value = "THURSDAY"),
    Fri(strId = R.string.common_friday, strId2 = R.string.common_friday_2, value = "FRIDAY"),
    Sat(strId = R.string.common_saturday, strId2 = R.string.common_saturday_2, value = "SATURDAY"),
    Sun(strId = R.string.common_sunday, strId2 = R.string.common_sunday_2, value = "SUNDAY"),
}