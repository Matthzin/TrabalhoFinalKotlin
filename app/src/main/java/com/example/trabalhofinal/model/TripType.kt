package com.example.trabalhofinal.model

enum class TripType(val descricao: String) {
    LAZER("Lazer"),
    NEGOCIOS("Negócios"),
    ESTUDOS("Estudos"),
    OUTROS("Outros");

    override fun toString(): String = descricao
}
