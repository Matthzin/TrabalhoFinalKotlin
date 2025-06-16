package com.example.trabalhofinal.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trabalhofinal.database.AppDatabase
import com.example.trabalhofinal.entity.Trip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.material3.SwipeToDismissBoxValue
import com.example.trabalhofinal.components.BottomNavigationBar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.trabalhofinal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onNavigateToRegisterTrip: () -> Unit
) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val trips = remember { mutableStateListOf<Trip>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        tripDao.getAllTrips().collectLatest {
            trips.clear()
            trips.addAll(it)
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
                        scope.launch {
                            tripDao.delete(trip)
                        }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCard(
    trip: Trip,
    onLongPress: () -> Unit,
    onDelete: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.5f },
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = { onLongPress() })
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val imageResId = when (trip.tripType) {
                        trip.tripType -> R.drawable.lazer
                        else -> R.drawable.lazer
                    }

                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Tipo de Viagem: ${trip.tripType.name}",
                        modifier = Modifier
                            .size(72.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Text("Destino: ${trip.destination}", style = MaterialTheme.typography.titleMedium)
                        Text("Tipo: ${trip.tripType.name}", style = MaterialTheme.typography.bodyMedium)

                        val formattedStartDate = try {
                            LocalDate.parse(trip.startDate.toString()).format(dateFormatter)
                        } catch (e: Exception) {
                            trip.startDate
                        }
                        Text("Início: $formattedStartDate", style = MaterialTheme.typography.bodyMedium)

                        val formattedEndDate = try {
                            LocalDate.parse(trip.endDate.toString()).format(dateFormatter)
                        } catch (e: Exception) {
                            trip.endDate
                        }
                        Text("Término: $formattedEndDate", style = MaterialTheme.typography.bodyMedium)

                        val formattedBudget = currencyFormatter.format(trip.budget)
                        Text("Orçamento: $formattedBudget", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    )
}