package com.example.trabalhofinal.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhofinal.dao.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userDao: UserDao) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onUserChange(user: String) {
        _uiState.value = _uiState.value.copy(user = user)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }

    fun login() {
        val user = _uiState.value.user
        val password = _uiState.value.password

        if (user.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Fill all the required fields")
            return
        }

        viewModelScope.launch {
            val result = userDao.getUserByCredentials(user, password)
            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "",
                    isLoggedIn = true
                )
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = "Invalid username or password")
            }
        }
    }

    fun clearLoginFlag() {
        _uiState.value = _uiState.value.copy(isLoggedIn = false)
    }

}

data class LoginState(
    val user: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val isLoggedIn: Boolean = false
)

