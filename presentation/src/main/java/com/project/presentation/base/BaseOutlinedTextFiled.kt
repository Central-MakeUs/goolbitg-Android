package com.project.presentation.base

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.white

@Composable
fun BaseOutlinedTextFiled(
    modifier: Modifier = Modifier,
    value: String,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable () -> Unit = {},
    shape: Shape = OutlinedTextFieldDefaults.shape,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = white,
        unfocusedTextColor = white,
        disabledTextColor = white,
        errorTextColor = white,
        focusedContainerColor = gray600,
        unfocusedContainerColor = gray600,
        disabledContainerColor = gray600,
        errorContainerColor = gray600,
        focusedBorderColor = gray500,
        unfocusedBorderColor = gray500,
        disabledBorderColor = gray500,
        errorBorderColor = com.project.presentation.ui.theme.error,
        cursorColor = com.project.presentation.ui.theme.error
    ),
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        textStyle = textStyle,
        placeholder = placeholder,
        maxLines = 1,
        colors = colors,
        shape = shape,
        keyboardOptions = keyboardOptions,
        onValueChange = onValueChange
    )
}
