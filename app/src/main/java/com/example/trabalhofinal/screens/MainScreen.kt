package com.example.trabalhofinal.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trabalhofinal.components.BottomNavigationBar
import com.example.trabalhofinal.components.TravelItineraryContent
import com.example.trabalhofinal.components.UniversalBottomSheetContent
import com.example.trabalhofinal.database.AppDatabase
import com.example.trabalhofinal.viewmodel.MainScreenViewModelFactory
import java.io.OutputStreamWriter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.example.trabalhofinal.components.TripCard
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onNavigateToRegisterTrip: () -> Unit
) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()

    val viewModel: MainScreenViewModel = viewModel(factory = MainScreenViewModelFactory(tripDao))

    val trips by viewModel.trips.collectAsState()
    val itineraryUiState by viewModel.itineraryUiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val showBottomSheet = itineraryUiState.trip != null

    val uiScope = rememberCoroutineScope()

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(itineraryUiState.itineraryText)
                    }
                    Toast.makeText(context, "Roteiro exportado com sucesso!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao exportar roteiro: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Viagens") },
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(trips, key = { it.id!! }) { trip ->
                TripCard(
                    trip = trip,
                    onLongPress = {
                        navController.navigate("editTrip/${trip.id}")
                    },
                    onDelete = {
                        viewModel.deleteTrip(trip)
                    },
                    onGenerateItinerary = { clickedTrip ->
                        viewModel.generateItineraryForTrip(clickedTrip)
                    }
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                uiScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        viewModel.resetItineraryUiState()
                    }
                }
            },
            sheetState = sheetState
        ) {
            UniversalBottomSheetContent(
                title = "Roteiro de Viagem para ${itineraryUiState.trip?.destination ?: ""}",
                onDismiss = {
                    uiScope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            viewModel.resetItineraryUiState()
                        }
                    }
                },
                content = {
                    TravelItineraryContent(
                        uiState = itineraryUiState,
                        onRetry = { viewModel.retryGenerateItinerary() },
                        onExportRequest = { fileName -> createDocumentLauncher.launch(fileName) }
                    )
                },
                actionButton = {
                    if (!itineraryUiState.isLoading && !itineraryUiState.errorOccurred && itineraryUiState.itineraryText.isNotBlank()) {
                        val destination = itineraryUiState.trip?.destination ?: "Viagem"
                        val encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8.toString())
                        val fileName = "roteiro_${encodedDestination}_${System.currentTimeMillis()}.txt"
                        Button(
                            onClick = { createDocumentLauncher.launch(fileName) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Exportar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exportar Roteiro (.txt)")
                        }
                    }
                }
            )
        }
    }
}