package com.example.travelapp.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            onDateSelected(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear))

        },
        year, month, day
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Selecionar data"
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { datePickerDialog.show() }
                .zIndex(1f)
        )
    }
}
