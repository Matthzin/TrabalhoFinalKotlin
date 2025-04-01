package com.example.trabalhofinal.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.trabalhofinal.entity.User

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Upsert
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM User u WHERE u.id = :id")
    suspend fun findById(id: Int): User?

    @Query("SELECT * FROM User")
    suspend fun findAll(): List<User>

    @Query("SELECT * FROM User u ORDER BY u.name ASC")
    suspend fun findAllByName(): List<User>
}