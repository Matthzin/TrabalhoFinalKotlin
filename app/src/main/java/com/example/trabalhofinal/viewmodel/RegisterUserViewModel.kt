package com.example.trabalhofinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhofinal.dao.UserDao
import com.example.trabalhofinal.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUser(
    val user: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String = "",
    val isSaved: Boolean = false
) {
    fun validateEmail(): String {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (email.isBlank()) {
            return "Insira um email"
        }
        if (!emailRegex.matches(email)) {
            return "Formato de email inválido. Use um formato válido (ex: user@example.com)"
        }
        return ""
    }

    fun validatePassord(): String {
        if (password.isBlank()) {
            return "Insira uma senha"
        }
        return ""
    }

    fun validateConfirmPassword(): String {
        if (confirmPassword != password) {
            return "A senhas não conferem"
        }
        return ""
    }

    fun validateAllField() {
        if (user.isBlank()) {
            throw Exception("Insira um usuário")
        }
        if (validateEmail().isNotBlank()) {
            throw Exception(validateEmail())
        }
        if (name.isBlank()) {
            throw Exception("Insira um nome")
        }
        if (email.isBlank()) {
            throw Exception("Insira um email")
        }
        if (validatePassord().isNotBlank()) {
            throw Exception(validatePassord())
        }
        if (validateConfirmPassword().isNotBlank()) {
            throw Exception(validateConfirmPassword())
        }
    }

    fun toUser(): User {
        return User(
            user = user,
            name = name,
            email = email,
            password = password
        )
    }

}

class RegisterUserViewModel(private val userDao: UserDao) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUser())
    val uiState : StateFlow<RegisterUser> = _uiState.asStateFlow()

    fun onUserChange(user: String) {
        _uiState.value = _uiState.value.copy(user = user)
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onEmailChange(email : String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onConfirmPassword(confirm : String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirm)
    }

    fun register() {
        try {
            _uiState.value.validateAllField()
            viewModelScope.launch {
                userDao.insert(_uiState.value.toUser())
                _uiState.value = _uiState.value.copy(isSaved = true)
            }
        }
        catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Unknow error")
        }
    }

    fun cleanDisplayValues() {
        _uiState.value = _uiState.value.copy(
            isSaved = false,
            errorMessage = ""
        )
    }

    fun cleanErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }


}