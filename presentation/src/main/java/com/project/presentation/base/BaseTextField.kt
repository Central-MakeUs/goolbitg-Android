package com.project.presentation.base

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white


@Composable
fun BaseTextField(
    value: String,
    placeHolderValue: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefixValue: String? = null,
    maxLength: Int? = null,
    textStyle: TextStyle = goolbitgTypography.caption2,
    textColor: Color = white,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    shape: Shape = RoundedCornerShape(6.dp),
    placeHolderColor: Color = gray300,
    containerColor: Color = gray600,
    cursorColor: Color = white,
    visualTransformation: VisualTransformation? = null
) {
    Box(modifier = modifier
        .clip(shape)
        .border(width = 1.dp, color = gray500, shape = shape)) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            singleLine = true,
            maxLines = 1,
            placeholder = {
                Text(
                    text = placeHolderValue,
                    style = textStyle,
                    color = placeHolderColor
                )
            },
            textStyle = textStyle,
            onValueChange = {
                if(maxLength == null || it.length <= maxLength){
                    onTextChanged(it)
                }
            },
            prefix = {
                if (prefixValue != null) {
                    Text(
                        text = prefixValue,
                        style = textStyle,
                        color = if (value.isNotEmpty()) textColor else placeHolderColor
                    )
                }
            },
            visualTransformation = visualTransformation ?: VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                disabledTextColor = textColor,
                errorTextColor = textColor,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor,
                errorContainerColor = containerColor,
                focusedIndicatorColor = transparent,
                unfocusedIndicatorColor = transparent,
                disabledIndicatorColor = transparent,
                errorIndicatorColor = transparent,
                cursorColor = cursorColor,
            ),
            keyboardOptions = keyboardOptions
        )
    }
}
