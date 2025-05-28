package com.example.trabalhofinal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.trabalhofinal.dao.TripDao
import com.example.trabalhofinal.dao.UserDao
import com.example.trabalhofinal.database.Converters
import com.example.trabalhofinal.entity.Trip
import com.example.trabalhofinal.entity.User

@Database(
    entities = [User::class, Trip::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "user_database")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE `Trip` " +
                    "ADD COLUMN IF NOT EXISTS `startDate` DATE NOT NULL DEFAULT CURRENT_DATE," +
                    "ADD COLUMN IF NOT EXISTS `endDate` DATE NOT NULL DEFAULT CURRENT_DATE"
        )
    }
}