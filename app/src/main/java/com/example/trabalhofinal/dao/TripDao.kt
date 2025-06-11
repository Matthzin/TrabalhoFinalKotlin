package com.example.trabalhofinal.dao

import androidx.room.*
import com.example.trabalhofinal.entity.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM Trip ORDER BY startDate DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM Trip WHERE id = :id LIMIT 1")
    suspend fun getTripById(id: kotlin.Int?): Trip?

    @Insert
    suspend fun insert(trip: Trip)

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)
}
