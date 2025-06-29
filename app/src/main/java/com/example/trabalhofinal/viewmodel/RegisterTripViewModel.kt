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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

data class RegisterTripUiState(
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val budget: Double = 0.0,
    val selectedTripType: TripType = TripType.LAZER,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterTripViewModel(private val tripDao: TripDao) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterTripUiState())
    val uiState: StateFlow<RegisterTripUiState> = _uiState.asStateFlow()

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

    fun saveTrip() {
        val currentUiState = _uiState.value

        if (currentUiState.destination.isBlank() ||
            currentUiState.startDate.isBlank() ||
            currentUiState.endDate.isBlank() ||
            currentUiState.budget <= 0.0
        ) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos!") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }

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

                val trip = Trip(
                    destination = currentUiState.destination,
                    tripType = currentUiState.selectedTripType,
                    startDate = startDateDate,
                    endDate = endDateDate,
                    budget = currentUiState.budget
                )

                tripDao.insert(trip)

                Log.d("RegisterTripViewModel", "Viagem salva: $trip")

                _uiState.update {
                    it.copy(
                        destination = "",
                        startDate = "",
                        endDate = "",
                        budget = 0.0,
                        selectedTripType = TripType.LAZER,
                        saveSuccess = true,
                        errorMessage = null,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("RegisterTripViewModel", "Erro ao salvar viagem", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao salvar viagem: ${e.localizedMessage ?: e.message}",
                        isLoading = false,
                        saveSuccess = false
                    )
                }
            }
        }
    }

    fun resetSaveStatus() {
        _uiState.update { it.copy(saveSuccess = false, errorMessage = null) }
    }
}