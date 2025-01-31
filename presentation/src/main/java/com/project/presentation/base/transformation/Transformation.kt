package com.project.presentation.base.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

object Transformation {

    // 천 단위 콤마 변환을 함수로 구현
    fun thousandSeparatorTransformation(): VisualTransformation {
        return VisualTransformation { text ->
            val originalText = text.text
            val formattedText = originalText.toLongOrNull()?.let {
                DecimalFormat("#,###").format(it)
            } ?: originalText

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return if (offset == 0) 0 else formattedText.length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return text.length
                }
            }

            TransformedText(AnnotatedString(formattedText), offsetMapping)
        }
    }
}