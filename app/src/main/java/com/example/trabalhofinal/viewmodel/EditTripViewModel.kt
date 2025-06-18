package com.example.trabalhofinal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhofinal.dao.TripDao
import com.example.trabalhofinal.entity.Trip
import com.example.trabalhofinal.model.TripType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

data class EditTripUiState(
    val tripId: Int? = null,
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val budget: Double = 0.0,
    val selectedTripType: TripType = TripType.LAZER,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val initialLoadComplete: Boolean = false
)

class EditTripViewModel(private val tripDao: TripDao) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTripUiState())
    val uiState: StateFlow<EditTripUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    fun loadTrip(id: Int?) {
        if (id == null) {
            _uiState.update { it.copy(errorMessage = "ID da viagem não fornecido.", initialLoadComplete = true) }
            return
        }

        if (_uiState.value.tripId == id && _uiState.value.initialLoadComplete) {
            return
        }

        _uiState.update { it.copy(isLoading = true, tripId = id, errorMessage = null) }

        viewModelScope.launch {
            try {
                val trip = tripDao.getTripById(id)
                trip?.let { existingTrip ->
                    _uiState.update {
                        it.copy(
                            tripId = existingTrip.id,
                            destination = existingTrip.destination,
                            selectedTripType = existingTrip.tripType,
                            startDate = dateFormat.format(existingTrip.startDate),
                            endDate = dateFormat.format(existingTrip.endDate),
                            budget = existingTrip.budget,
                            isLoading = false,
                            initialLoadComplete = true
                        )
                    }
                } ?: run {
                    _uiState.update { it.copy(errorMessage = "Viagem não encontrada.", isLoading = false, initialLoadComplete = true) }
                }
            } catch (e: Exception) {
                Log.e("EditTripViewModel", "Erro ao carregar viagem", e)
                _uiState.update { it.copy(errorMessage = "Erro ao carregar viagem: ${e.message}", isLoading = false, initialLoadComplete = true) }
            }
        }
    }

    fun updateDestination(newDestination: String) {
        _uiState.update { it.copy(destination = newDestination, errorMessage = null) }
    }

    fun updateStartDate(newDate: String) {
        _uiState.update { it.copy(startDate = newDate, errorMessage = null) }
    }

    fun updateEndDate(newDate: String) {
        _uiState.update { it.copy(endDate = newDate, errorMessage = null) }
    }

    fun updateBudget(newBudget: Double) {
        _uiState.update { it.copy(budget = newBudget, errorMessage = null) }
    }

    fun updateSelectedTripType(newType: TripType) {
        _uiState.update { it.copy(selectedTripType = newType, errorMessage = null) }
    }

    fun saveChanges() {
        val currentUiState = _uiState.value

        if (currentUiState.tripId == null ||
            currentUiState.destination.isBlank() ||
            currentUiState.startDate.isBlank() ||
            currentUiState.endDate.isBlank() ||
            currentUiState.budget <= 0.0
        ) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos e certifique-se que o orçamento é válido.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }

        viewModelScope.launch {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val startDateParsed = LocalDate.parse(currentUiState.startDate, formatter)
                val endDateParsed = LocalDate.parse(currentUiState.endDate, formatter)

                val startDateDate = Date.from(
                    startDateParsed.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
                val endDateDate = Date.from(
                    endDateParsed.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )

                val updatedTrip = Trip(
                    id = currentUiState.tripId,
                    destination = currentUiState.destination,
                    tripType = currentUiState.selectedTripType,
                    startDate = startDateDate,
                    endDate = endDateDate,
                    budget = currentUiState.budget
                )

                tripDao.update(updatedTrip)
                Log.d("EditTripViewModel", "Viagem atualizada: $updatedTrip")

                _uiState.update {
                    it.copy(
                        saveSuccess = true,
                        errorMessage = null,
                        isSaving = false
                    )
                }
            } catch (e: Exception) {
                Log.e("EditTripViewModel", "Erro ao atualizar viagem", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao atualizar viagem: ${e.localizedMessage ?: e.message}",
                        isSaving = false,
                        saveSuccess = false
                    )
                }
            }
        }
    }

    fun resetStatus() {
        _uiState.update { it.copy(saveSuccess = false, errorMessage = null) }
    }
}