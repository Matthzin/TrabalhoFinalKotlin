package com.example.trabalhofinal.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CurrencyInputField(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit = { Text("Orçamento") },
    initialValue: Double = 0.0,
    onValueChange: (Double) -> Unit
) {
    var amountInCents by remember { mutableLongStateOf((initialValue * 100).toLong()) }
    var textFieldValue by remember(amountInCents) {
        val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            .format(amountInCents / 100.0)
        mutableStateOf(TextFieldValue(formatted, TextRange(formatted.length)))
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newTextFieldValue ->
            val cleanString = newTextFieldValue.text.replace(Regex("[^\\d]"), "")
            if (cleanString.isEmpty()) {
                amountInCents = 0L
            } else {
                amountInCents = try {
                    cleanString.toLong()
                } catch (e: NumberFormatException) {
                    Long.MAX_VALUE
                }
            }
            val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                .format(amountInCents / 100.0)

            textFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
            onValueChange(amountInCents / 100.0)
        },
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier
    )
}