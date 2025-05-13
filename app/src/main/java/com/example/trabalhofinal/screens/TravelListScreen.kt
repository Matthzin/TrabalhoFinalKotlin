package com.example.trabalhofinal.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.database.AppDatabase
import com.example.trabalhofinal.entity.Trip
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TravelListScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val trips = remember { mutableStateListOf<Trip>() }

    LaunchedEffect(true) {
        tripDao.getAllTrips().collectLatest {
            trips.clear()
            trips.addAll(it)
        }
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(trips) { trip ->
            TripCard(trip = trip, onLongPress = {
                // TODO: Navegar para tela de edição
            })
        }
    }
}

@Composable
fun TripCard(trip: Trip, onLongPress: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Destino: ${trip.destination}", style = MaterialTheme.typography.titleMedium)
            Text("Tipo: ${trip.tripType}")
            Text("Início: ${trip.startDate}")
            Text("Término: ${trip.endDate}")
            Text("Orçamento: R$ ${trip.budget}")
        }
    }
}
