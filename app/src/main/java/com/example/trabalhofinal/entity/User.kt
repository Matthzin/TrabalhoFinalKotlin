package com.example.trabalhofinal.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)val id: Int = 0,
    val user: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = ""
) {
}