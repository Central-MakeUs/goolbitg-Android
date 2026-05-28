package com.project.presentation.base

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DOUBLE_BACK_PRESS_INTERVAL_MS = 1000L
private const val SNACKBAR_DISMISS_DELAY_MS = 3000L
private const val EXIT_MESSAGE = "종료하시려면 한 번 더 눌러주세요."

@Composable
fun ExitOnDoubleBackPress(snackBarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        if (System.currentTimeMillis() - backPressedTime <= DOUBLE_BACK_PRESS_INTERVAL_MS) {
            (context as Activity).finish()
        } else {
            coroutineScope.launch {
                val job = launch {
                    snackBarHostState.showSnackbar(
                        message = EXIT_MESSAGE,
                        withDismissAction = true,
                    )
                }
                delay(SNACKBAR_DISMISS_DELAY_MS)
                job.cancel()
            }
        }
        backPressedTime = System.currentTimeMillis()
    }
}
