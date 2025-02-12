package com.project.presentation.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.navigation.NavItem
import com.project.presentation.onboarding.OnboardingViewModel
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Preview
fun AnalysisConsumeTypeScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            viewModel.getUserInfo()
        }
    }

    LaunchedEffect(state.value.isAnalysisSuccess) {
        if(state.value.isAnalysisSuccess){
            coroutineScope.launch {
                navHostController.navigate(NavItem.ShowConsumeType.route)
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(bg1)) {
        Scaffold(containerColor = transparent) { innerPadding ->
            AnalysisConsumeTypeContent(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun AnalysisConsumeTypeContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.analysis_consume_type_title),
            style = goolbitgTypography.h1,
            color = white
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.analysis_consume_type_sub_title),
            style = goolbitgTypography.body1,
            color = gray300
        )

        Spacer(modifier = Modifier.height(65.dp))

        BaseIcon(
            modifier = Modifier.fillMaxWidth(),
            iconId = R.drawable.illu_login_logo)
    }
}