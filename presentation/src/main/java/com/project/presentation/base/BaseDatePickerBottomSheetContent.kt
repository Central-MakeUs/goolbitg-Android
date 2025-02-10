package com.project.presentation.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.project.presentation.R
import java.time.LocalDate

@Composable
fun BaseDatePickerBottomSheetContent(
    initYear: String,
    initMonth: String,
    initDay: String,
    yearList: List<String>,
    modifier: Modifier = Modifier,
    onConfirm: (LocalDate) -> Unit
) {
    var year by remember { mutableStateOf(initYear) }
    var month by remember { mutableStateOf(initMonth) }
    var day by remember { mutableStateOf(initDay) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 26.dp)
    ) {
        BaseDatePicker(
            initYear = initYear,
            initMonth = initMonth,
            initDay = initDay,
            yearList = yearList,
            onYearChanged = {
                year = it
            },
            onMonthChanged = {
                month = it
            },
            onDayChanged = {
                day = it
            },
        )
        Spacer(modifier = Modifier.height(42.dp))
        BaseBottomBtn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(R.string.common_apply),
            onClick = { onConfirm(LocalDate.of(year.toInt(), month.toInt(), day.toInt())) }
        )
        // 이거 안해주면 시스템 네비게이션바랑 겹침
        Spacer(
            modifier =
            Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
        )
    }
}