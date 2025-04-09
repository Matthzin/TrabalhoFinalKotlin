package com.example.trabalhofinal.screens

import androidx.lifecycle.ViewModel
import com.example.trabalhofinal.dao.UserDao
import com.example.trabalhofinal.entity.User
import kotlinx.coroutines.flow.Flow

class TravelListViewModel(private val userDao: UserDao) : ViewModel() {
    val users: Flow<List<User>> = userDao.findAll()
}