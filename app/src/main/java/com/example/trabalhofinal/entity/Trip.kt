package com.example.trabalhofinal.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trabalhofinal.model.TripType
import java.util.Date

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destination: String,
    val tripType: TripType,
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val budget: Double = 0.0
)
