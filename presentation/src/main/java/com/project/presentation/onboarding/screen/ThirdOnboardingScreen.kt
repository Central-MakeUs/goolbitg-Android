package com.project.presentation.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@Composable
@Preview
fun ThirdOnboardingScreenScreen(navHostController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            ThirdOnboardingBody(modifier = Modifier.padding(innerPadding))
        }
    }

}

@Composable
fun ThirdOnboardingBody(modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(top = 36.dp, start = 24.dp, end = 24.dp)) {

        val text = stringResource(R.string.onboarding_third_title)
        val highlightWord = stringResource(R.string.onboarding_third_sub_title_highlight)
        val annotatedString = buildAnnotatedString {
            val startIndex = text.indexOf(highlightWord)
            append(text)
            if (startIndex != -1) {
                addStyle(
                    style = SpanStyle(color = Color(0xFF4BB329)),
                    start = startIndex,
                    end = startIndex + highlightWord.length
                )
            }
        }
        Text(
            text = annotatedString,
            style = goolbitgTypography.h1,
            color = white
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_third_sub_title),
            style = goolbitgTypography.body1,
            color = gray300
        )

        Spacer(modifier = Modifier.height(40.dp))


    }
}

@Composable
fun OnboardingCheckListItem(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if(isSelected){

    }else{

    }
    Row(modifier = Modifier.fillMaxWidth()) { }
}