package com.example.trabalhofinal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhofinal.BuildConfig
import com.example.trabalhofinal.dao.TripDao
import com.example.trabalhofinal.entity.Trip
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ItineraryBottomSheetUiState(
    val isLoading: Boolean = false,
    val errorOccurred: Boolean = false,
    val itineraryText: String = "",
    val trip: Trip? = null,
    val chatHistory: MutableList<Content> = mutableListOf(),
    val userMessage: String = ""
)

class MainScreenViewModel(private val tripDao: TripDao) : ViewModel() {

    val trips: StateFlow<List<Trip>> = tripDao.getAllTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _itineraryUiState = MutableStateFlow(ItineraryBottomSheetUiState())
    val itineraryUiState: StateFlow<ItineraryBottomSheetUiState> = _itineraryUiState.asStateFlow()

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                tripDao.delete(trip)
                Log.d("MainScreenViewModel", "Viagem deletada: ${trip.destination}")
            } catch (e: Exception) {
                Log.e("MainScreenViewModel", "Erro ao deletar viagem: ${trip.destination}", e)
            }
        }
    }

    fun generateItineraryForTrip(trip: Trip) {
        _itineraryUiState.update {
            it.copy(
                isLoading = true,
                errorOccurred = false,
                itineraryText = "Gerando roteiro para ${trip.destination}...",
                trip = trip,
                chatHistory = mutableListOf(),
                userMessage = ""
            )
        }

        viewModelScope.launch {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

            val formattedStartDate = try { trip.startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter) } catch (e: Exception) { "Data inválida" }
            val formattedEndDate = try { trip.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter) } catch (e: Exception) { "Data inválida" }
            val formattedBudget = currencyFormatter.format(trip.budget)

            try {
                val prompt = """
                    Crie um roteiro de viagem detalhado para ${trip.destination},
                    com foco em uma viagem de ${trip.tripType.descricao}.
                    O período da viagem é de $formattedStartDate a $formattedEndDate.
                    O orçamento total disponível para a viagem é de $formattedBudget.
                    O roteiro deve incluir sugestões de atividades diárias (manhã, tarde, noite),
                    opções de alimentação (café da manhã, almoço, jantar) e dicas úteis para o local.
                    Divida o roteiro por dias, com títulos claros para cada dia e subtítulos para as seções (Atividades, Alimentação, Dicas).
                    Seja criativo e prático, considerando o orçamento informado.
                    Não inclua nenhuma saudação, introdução ou despedida. Apenas o roteiro formatado.
                """.trimIndent()

                val chat = generativeModel.startChat(history = _itineraryUiState.value.chatHistory)
                val response = chat.sendMessage(prompt)
                val generatedText = response.text ?: "Não foi possível gerar o roteiro."

                _itineraryUiState.update {
                    it.copy(
                        isLoading = false,
                        itineraryText = generatedText,
                        chatHistory = chat.history.toMutableList()
                    )
                }
            } catch (e: Exception) {
                Log.e("MainScreenViewModel", "Erro ao gerar roteiro Gemini", e)
                _itineraryUiState.update {
                    it.copy(
                        isLoading = false,
                        errorOccurred = true,
                        itineraryText = "Erro ao gerar roteiro: ${e.message}. Verifique sua conexão ou API Key."
                    )
                }
            }
        }
    }

    fun updateMessage(message: String) {
        _itineraryUiState.update { it.copy(userMessage = message) }
    }

    fun sendMessage() {
        val currentTrip = _itineraryUiState.value.trip
        val userMessage = _itineraryUiState.value.userMessage

        if (currentTrip == null || userMessage.isBlank()) {
            return
        }

        _itineraryUiState.update {
            it.copy(
                isLoading = true,
                errorOccurred = false,
                itineraryText = "Refinando roteiro com sua sugestão...",
                userMessage = ""
            )
        }

        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(history = _itineraryUiState.value.chatHistory)
                val response = chat.sendMessage(userMessage)
                val generatedText = response.text ?: "Não foi possível refinar o roteiro."

                _itineraryUiState.update {
                    it.copy(
                        isLoading = false,
                        itineraryText = generatedText,
                        chatHistory = chat.history.toMutableList()
                    )
                }
            } catch (e: Exception) {
                Log.e("MainScreenViewModel", "Erro ao refinar roteiro Gemini", e)
                _itineraryUiState.update {
                    it.copy(
                        isLoading = false,
                        errorOccurred = true,
                        itineraryText = "Erro ao refinar roteiro: ${e.message}. Tente novamente."
                    )
                }
            }
        }
    }

    fun resetItineraryUiState() {
        _itineraryUiState.update { ItineraryBottomSheetUiState() }
    }

    fun retryGenerateItinerary() {
        _itineraryUiState.value.trip?.let { trip ->
            if (_itineraryUiState.value.chatHistory.isEmpty()) {
                generateItineraryForTrip(trip)
            } else {
                generateItineraryForTrip(trip)
            }
        }
    }
}