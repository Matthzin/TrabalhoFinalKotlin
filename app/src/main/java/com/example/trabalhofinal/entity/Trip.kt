package com.example.trabalhofinal.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destination: String,
    val tripType: String,
    val startDate: String,
    val endDate: String,
    val budget: Double
)
