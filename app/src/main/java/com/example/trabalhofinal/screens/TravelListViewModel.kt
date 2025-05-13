package com.example.trabalhofinal.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhofinal.dao.TripDao
import com.example.trabalhofinal.entity.Trip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TravelListViewModel(private val tripDao: TripDao) : ViewModel() {
    val trips: StateFlow<List<Trip>> = tripDao.getAllTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
