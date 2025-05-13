package com.example.trabalhofinal.model

enum class TripType(val label: String) {
    LEISURE("Lazer"),
    BUSINESS("Negócios"),
    STUDY("Estudos"),
    OTHER("Outro");

    override fun toString(): String = label
}
