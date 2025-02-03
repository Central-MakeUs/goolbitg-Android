package com.project.presentation.base

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.white
import java.time.LocalDate

@SuppressLint("DefaultLocale")
@Composable
fun BaseDatePicker(
    year: Int,
    month: Int,
    day: Int,
    yearList: List<Int>,
    monthList: List<Int>,
    dayList: List<Int>,
    modifier: Modifier = Modifier,
    onYearChanged: (String) -> Unit,
    onMonthChanged: (String) -> Unit,
    onDayChanged: (String) -> Unit,
) {
    val years = remember { (1..12).map { String.format("%02d", it) } }
    val yearsPickerState = rememberPickerState()
    val months = remember { (1..59).map { String.format("%02d", it) } }
    val monthsPickerState = rememberPickerState()
    val days = remember { listOf("AM", "PM") }
    val daysPickerState = rememberPickerState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Row(
                modifier = Modifier
                    .width(180.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BaseInfinityPicker(
                    state = yearsPickerState,
                    items = years,
                    modifier = Modifier.width(80.dp),
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = onYearChanged
                )
                BaseInfinityPicker(
                    state = monthsPickerState,
                    items = months,
                    modifier = Modifier.width(44.dp),
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = onMonthChanged
                )
                BaseInfinityPicker(
                    state = daysPickerState,
                    items = days,
                    modifier = Modifier.width(48.dp),
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = onDayChanged
                )
            }

            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(40.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(roundSm))
                    .background(
                        white.copy(alpha = 0.1f)
                    )
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BaseDatePicker(
    yearList: List<String>,
    modifier: Modifier = Modifier,
    onYearChanged: (String) -> Unit,
    onMonthChanged: (String) -> Unit,
    onDayChanged: (String) -> Unit,
) {
    // Remember states for each picker
    val yearsPickerState = rememberPickerState(yearList.last())
    val monthsPickerState = rememberPickerState("1")
    val daysPickerState = rememberPickerState("1")

    // Remember the dynamic day list based on year and month
    var dayList by remember { mutableStateOf(generateDays(yearsPickerState.selectedItem.toInt(), monthsPickerState.selectedItem.toInt())) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Row(
                modifier = Modifier
                    .width(220.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasePicker(
                    state = yearsPickerState,
                    items = yearList.map { it },
                    modifier = Modifier.width(100.dp),
                    suffixStr = "년",
                    startIndex = yearList.indexOf(yearsPickerState.selectedItem),
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = { selectedYear ->
                        onYearChanged(selectedYear)
                        dayList = generateDays(selectedYear.toInt(), monthsPickerState.selectedItem.toInt())
                    }
                )
                BasePicker(
                    state = monthsPickerState,
                    items = (1..12).map { it.toString() },
                    modifier = Modifier.width(60.dp),
                    suffixStr = "월",
                    startIndex = (1..12).map { it.toString() }.indexOf(monthsPickerState.selectedItem),
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = { selectedMonth ->
                        onMonthChanged(selectedMonth)
                        dayList = generateDays(yearsPickerState.selectedItem.toInt(), selectedMonth.toInt())
                    }
                )
                BasePicker(
                    state = daysPickerState,
                    items = dayList.map { it.toString() },
                    modifier = Modifier.width(60.dp),
                    suffixStr = "일",
                    startIndex = dayList.map { it.toString() }.indexOf(daysPickerState.selectedItem),
                    textModifier = Modifier.padding(4.dp),
                    textStyle = goolbitgTypography.h2.copy(color = white),
                    onItemChanged = onDayChanged
                )
            }

            Box(
                modifier = Modifier
                    .width(260.dp)
                    .height(40.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(roundSm))
                    .background(
                        white.copy(alpha = 0.1f)
                    )
            )
        }
    }
}

// Function to calculate valid days based on the year and month
private fun generateDays(year: Int, month: Int): List<Int> {
    return try {
        val daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()
        (1..daysInMonth).toList()
    } catch (e: Exception) {
        emptyList() // Return an empty list in case of invalid inputs
    }
}
