package com.project.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.transparent

@Composable
@Preview
fun LoginScreen(
    navHostController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
    ) {
        Scaffold(containerColor = transparent) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    BaseIcon(
                        modifier = Modifier.align(Alignment.Center),
                        iconId = R.drawable.illu_login_logo
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFBE300))
                        .clickable {
                            navHostController.navigate(NavItem.IntroPermission.route)
//                            loginViewModel.loginWithKakaoTalk(context = context)
                        }
                        .padding(vertical = 10.dp),
                    text = "카카오톡으로 시작하기",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(160.dp))
            }
        }
    }
}