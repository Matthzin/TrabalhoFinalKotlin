package com.example.trabalhofinal.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhofinal.dao.TripDao
import com.example.trabalhofinal.entity.Trip
import kotlinx.coroutines.launch

class RegisterTripViewModel(private val tripDao: TripDao) : ViewModel() {

    fun insertTrip(trip: Trip) {
        viewModelScope.launch {
            tripDao.insert(trip)
        }
    }
}
