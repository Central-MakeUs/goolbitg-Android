package com.project.presentation.base.extension

import android.icu.text.DecimalFormat

object StringExtension {
    fun Int.priceComma(): String{
        val format = DecimalFormat("#,###")
        return format.format(this)
    }
}
