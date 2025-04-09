package com.example.trabalhofinal.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.trabalhofinal.entity.User
import kotlinx.coroutines.flow.Flow

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
    fun findAll(): Flow<List<User>>

    @Query("SELECT * FROM User u ORDER BY u.name ASC")
    suspend fun findAllByName(): List<User>

    @Query("SELECT * FROM user WHERE user = :username AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(username: String, password: String): User?
}