package com.example.trabalhofinal.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trabalhofinal.components.AppTopBar
import com.example.trabalhofinal.database.AppDatabase
import com.example.trabalhofinal.entity.Trip
import com.example.trabalhofinal.model.TripType
import com.example.travelapp.components.DatePickerField
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripScreen(tripId: Int?, navController: NavController) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val scope = rememberCoroutineScope()

    // Campos de estado
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedTripType by remember { mutableStateOf(TripType.LAZER) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Carrega os dados ao abrir a tela
    LaunchedEffect(tripId) {
        val trip = tripDao.getTripById(tripId)
        trip?.let {
            destination = it.destination
            selectedTripType = it.tripType

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            startDate = dateFormat.format(it.startDate)
            endDate = dateFormat.format(it.endDate)

            val formattedBudget = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(it.budget)
            budget = formattedBudget
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTopBar(title = "Editar Viagem", onNavigationClick = { navController.popBackStack() })

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )

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

        OutlinedTextField(
            value = budget,
            onValueChange = {
                val cleanString = it.replace(Regex("[^\\d]"), "")
                val parsed = cleanString.toDoubleOrNull() ?: 0.0
                val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                    .format(parsed / 100)
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
                    return@Button
                }

                isLoading = true
                scope.launch {
                    try {
                        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val start = java.time.LocalDate.parse(startDate, formatter)
                        val end = java.time.LocalDate.parse(endDate, formatter)

                        val startDateParsed = Date.from(start.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
                        val endDateParsed = Date.from(end.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())

                        val parsedBudget = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                            .parse(budget)?.toDouble() ?: 0.0

                        val updatedTrip = Trip(
                            id = tripId,
                            destination = destination,
                            tripType = selectedTripType,
                            startDate = startDateParsed,
                            endDate = endDateParsed,
                            budget = parsedBudget
                        )

                        tripDao.update(updatedTrip)
                        Toast.makeText(context, "Viagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro ao atualizar viagem: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Salvando..." else "Salvar Alterações")
        }
    }
}

