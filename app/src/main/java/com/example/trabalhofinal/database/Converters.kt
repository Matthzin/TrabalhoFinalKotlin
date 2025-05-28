package com.example.trabalhofinal.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromDate(date: Date?): String? {

        return date?.toInstant()
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDate()
            ?.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toDate(dateString: String?): Date? {
        return dateString?.let {
            val localDate = LocalDate.parse(it, formatter)
            Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }
    }
}