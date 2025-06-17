package com.example.trabalhofinal.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.trabalhofinal.R // Certifique-se de que este import está correto
import com.example.trabalhofinal.entity.Trip // Certifique-se de que este import está correto
import com.example.trabalhofinal.model.TripType // Certifique-se de que este import está correto
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material.icons.filled.AutoAwesome // Importe o ícone do Gemini

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCard(
    trip: Trip,
    onLongPress: () -> Unit,
    onDelete: () -> Unit,
    onGenerateItinerary: (Trip) -> Unit // Mantenha este callback
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

    val animatedBackgroundAlpha by animateFloatAsState(
        targetValue = if (swipeState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
            (swipeState.progress / 0.5f).coerceIn(0f, 1f)
        } else {
            0f
        },
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
    )

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
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
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
                Box( // Adicione um Box aqui para permitir posicionamento de filhos
                    modifier = Modifier.fillMaxWidth()
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
                            // Removido o 'else' redundante, já que OUTROS é o último na enum e pode servir como fallback
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

                    // Ícone do Gemini no canto inferior direito do Card
                    IconButton(
                        onClick = { onGenerateItinerary(trip) }, // Chamar o callback com a viagem
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Alinha ao canto inferior direito do Box
                            .padding(8.dp) // Adiciona um padding para não ficar colado na borda
                            .size(36.dp) // Tamanho do ícone
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome, // Ícone de "estrelas" ou "magia" para IA/Gemini
                            contentDescription = "Gerar Roteiro com Gemini",
                            tint = MaterialTheme.colorScheme.tertiary // Uma cor que contraste bem
                        )
                    }
                }
            }
        }
    )
}