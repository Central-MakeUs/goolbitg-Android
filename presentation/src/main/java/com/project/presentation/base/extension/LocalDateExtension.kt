package com.project.presentation.base.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateExtension {
    private val halfFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val koYmdFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

    fun LocalDate.toHalfFormat(): String = this.format(halfFormatter)
    fun LocalDate.toKoYmdFormatter(): String = this.format(koYmdFormatter)
}