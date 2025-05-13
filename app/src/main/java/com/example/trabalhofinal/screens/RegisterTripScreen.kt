package com.example.trabalhofinal.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.model.TripType
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTripScreen(onRegisterSuccess: () -> Unit) {
    val context = LocalContext.current

    // Estados
    var destination by remember { mutableStateOf("") }
    var selectedTripType by remember { mutableStateOf(TripType.LEISURE) }
    var expanded by remember { mutableStateOf(false) }

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var budget by remember { mutableStateOf("") }

    // Formatadores
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Destino
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tipo de Viagem (Dropdown)
        Box {
            OutlinedTextField(
                value = selectedTripType.label,
                onValueChange = {},
                label = { Text("Tipo de Viagem") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                readOnly = true
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                TripType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.label) },
                        onClick = {
                            selectedTripType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Data de início
        DatePickerField(
            label = "Data de Início",
            date = startDate,
            onDateSelected = { startDate = it },
            context = context
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Data de término
        DatePickerField(
            label = "Data de Término",
            date = endDate,
            onDateSelected = { endDate = it },
            context = context
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Orçamento com máscara de moeda
        OutlinedTextField(
            value = budget,
            onValueChange = {
                val clean = it.replace("[^\\d]".toRegex(), "")
                val parsed = clean.toDoubleOrNull()?.div(100) ?: 0.0
                budget = currencyFormatter.format(parsed)
            },
            label = { Text("Orçamento") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (destination.isNotBlank() && startDate != null && endDate != null && budget.isNotBlank()) {
                    // Aqui pode salvar os dados como objeto de viagem
                    val createdAt = LocalDateTime.now()
                    Toast.makeText(context, "Viagem cadastrada em $createdAt", Toast.LENGTH_SHORT).show()
                    onRegisterSuccess()
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar Viagem")
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    date: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    context: Context
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val dateText = date?.format(dateFormatter) ?: ""

    OutlinedTextField(
        value = dateText,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        onDateSelected(LocalDate.of(y, m + 1, d))
                    },
                    year,
                    month,
                    day
                ).show()
            }
    )
}
