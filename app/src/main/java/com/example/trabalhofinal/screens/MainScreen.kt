package com.example.trabalhofinal.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.trabalhofinal.viewmodel.MainScreenViewModel
import com.example.trabalhofinal.viewmodel.MainScreenViewModelFactory
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
        val sortedTrips = remember(trips) {
            trips.sortedBy { it.startDate }
        }

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
            items(sortedTrips, key = { it.id!! }) { trip ->
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
                        onUserMessageChange = { viewModel.updateMessage(it) },
                        onSendMessage = { viewModel.sendMessage() }
                    )
                },
                actionButton = null
            )
        }
    }
}