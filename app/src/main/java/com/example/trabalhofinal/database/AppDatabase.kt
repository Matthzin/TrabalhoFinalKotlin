package com.example.trabalhofinal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trabalhofinal.dao.TripDao
import com.example.trabalhofinal.dao.UserDao
import com.example.trabalhofinal.entity.Trip
import com.example.trabalhofinal.entity.User

@Database(entities = [User::class, Trip::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { Instance = it }
            }
        }
    }
}
