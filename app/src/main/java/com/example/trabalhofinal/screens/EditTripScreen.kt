package com.example.trabalhofinal.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trabalhofinal.components.AppTopBar
import com.example.trabalhofinal.components.BottomNavigationBar
import com.example.trabalhofinal.components.CurrencyInputField
import com.example.trabalhofinal.database.AppDatabase
import com.example.trabalhofinal.model.TripType
import com.example.trabalhofinal.viewmodel.EditTripViewModel
import com.example.trabalhofinal.viewmodel.EditTripViewModelFactory
import com.example.travelapp.components.DatePickerField
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripScreen(
    tripId: Int?,
    navController: NavController,
    onTripUpdated: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val viewModel: EditTripViewModel = viewModel(factory = EditTripViewModelFactory(tripDao))
    val uiState by viewModel.uiState.collectAsState()
    var expandedDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.resetStatus()
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, "Viagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            viewModel.resetStatus()
            onTripUpdated()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Editar Viagem",
                showBackButton = true,
                onNavigationClick = onBack
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->

        if (uiState.isLoading && !uiState.initialLoadComplete) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.tripId == null && uiState.initialLoadComplete && uiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.errorMessage ?: "Erro ao carregar dados da viagem.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = uiState.destination,
                    onValueChange = { viewModel.updateDestination(it) },
                    label = { Text("Destino") },
                    modifier = Modifier.fillMaxWidth()
                )

                DatePickerField(
                    label = "Data de Início",
                    date = uiState.startDate,
                    onDateSelected = { viewModel.updateStartDate(it) }
                )

                DatePickerField(
                    label = "Data de Término",
                    date = uiState.endDate,
                    onDateSelected = { viewModel.updateEndDate(it) }
                )

                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.selectedTripType.descricao,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Viagem") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        TripType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.descricao) },
                                onClick = {
                                    viewModel.updateSelectedTripType(type)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                CurrencyInputField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Orçamento") },
                    initialValue = uiState.budget,
                    onValueChange = { newBudget -> viewModel.updateBudget(newBudget) }
                )

                Button(
                    onClick = {
                        viewModel.saveChanges()
                    },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Salvar Alterações")
                    }
                }
            }
        }
    }
}