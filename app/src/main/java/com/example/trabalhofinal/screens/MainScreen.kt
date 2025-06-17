package com.example.trabalhofinal.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.example.trabalhofinal.database.AppDatabase
import com.example.trabalhofinal.entity.Trip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.input.pointer.pointerInput
import com.example.trabalhofinal.components.BottomNavigationBar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneId
import com.example.trabalhofinal.R
import com.example.trabalhofinal.model.TripType

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
        tripDao.getAllTrips().collectLatest { fetchedTrips ->
            val sortedTrips = fetchedTrips.sortedBy { it.startDate }
            trips.clear()
            trips.addAll(sortedTrips)
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
        positionalThreshold = { it * 0.5f }, // Dismiss after 50% swipe
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

    // --- Background Alpha Animation ---
    val animatedBackgroundAlpha by animateFloatAsState(
        targetValue = if (swipeState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
            (swipeState.progress / 0.5f).coerceIn(0f, 1f)
        } else {
            0f
        },
        // AQUI: Adicionado easing para suavidade
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing) // Aumentei um pouco a duração para 200ms
    )

    // --- Icon Scale and Alpha Animation ---
    val iconActivationThreshold = 0.3f
    val iconFullVisibilityThreshold = 0.45f

    val iconAnimationProgress by remember(swipeState.progress) {
        val currentProgress = swipeState.progress
        val calculatedProgress = if (currentProgress > iconActivationThreshold) {
            ((currentProgress - iconActivationThreshold) / (iconFullVisibilityThreshold - iconActivationThreshold)).coerceIn(0f, 1f)
        } else {
            0f
        }
        mutableStateOf(calculatedProgress)
    }

    val animatedIconAlphaAndScale by animateFloatAsState(
        targetValue = iconAnimationProgress,
        // AQUI: Adicionado easing para suavidade
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing) // Aumentei um pouco a duração para 150ms
    )

    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = animatedBackgroundAlpha))
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val isSwipingActive = swipeState.currentValue != SwipeToDismissBoxValue.Settled ||
                        swipeState.targetValue != SwipeToDismissBoxValue.Settled

                if (isSwipingActive) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier
                            .alpha(animatedIconAlphaAndScale)
                            .scale(0.8f + (animatedIconAlphaAndScale * 0.2f))
                    )
                }
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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val imageResId = when (trip.tripType) {
                        TripType.LAZER -> R.drawable.lazer
                        TripType.NEGOCIOS -> R.drawable.negocios
                        TripType.ESTUDOS -> R.drawable.estudos
                        TripType.OUTROS -> R.drawable.outros
                        else -> R.drawable.outros // Fallback
                    }

                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Tipo de Viagem: ${trip.tripType.descricao}",
                        modifier = Modifier
                            .size(72.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Fit
                    )

                    Column {
                        Text("Destino: ${trip.destination}", style = MaterialTheme.typography.titleMedium)
                        Text("Tipo: ${trip.tripType.descricao}", style = MaterialTheme.typography.bodyMedium)

                        val formattedStartDate = try {
                            trip.startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "Data inválida"
                        }
                        Text("Início: $formattedStartDate", style = MaterialTheme.typography.bodyMedium)

                        val formattedEndDate = try {
                            trip.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "Data inválida"
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