package com.example.trabalhofinal.dao

import androidx.room.*
import com.example.trabalhofinal.entity.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM Trip")
    fun getAllTrips(): Flow<List<Trip>>

    @Insert
    suspend fun insert(trip: Trip)

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)
}
