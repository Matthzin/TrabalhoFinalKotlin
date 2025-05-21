package com.example.trabalhofinal.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.model.TripType
import com.example.travelapp.components.DatePickerField
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTripScreen(
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current

    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    var selectedTripType by remember { mutableStateOf(TripType.LAZER) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cadastro de Viagem", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )

        // Usando o novo componente de calendário
        DatePickerField(
            label = "Data de Início",
            date = startDate,
            onDateSelected = { startDate = it }
        )

        DatePickerField(
            label = "Data de Término",
            date = endDate,
            onDateSelected = { endDate = it }
        )

        // Dropdown para tipo de viagem
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedTripType.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Viagem") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                TripType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            selectedTripType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        // Campo de orçamento com máscara monetária
        OutlinedTextField(
            value = budget,
            onValueChange = {
                val cleanString = it.replace(Regex("[^\\d]"), "")
                val parsed = cleanString.toDoubleOrNull() ?: 0.0
                val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed / 100)
                budget = formatted
            },
            label = { Text("Orçamento") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (destination.isBlank() || startDate.isBlank() || endDate.isBlank() || budget.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                } else {
                    // Aqui você pode salvar no banco, se desejar
                    Toast.makeText(context, "Viagem cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
                    onRegisterSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar Viagem")
        }
    }
}
