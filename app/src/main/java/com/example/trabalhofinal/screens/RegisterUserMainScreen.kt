package com.example.trabalhofinal.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhofinal.components.ErrorDialog
import com.example.trabalhofinal.components.MyPasswordField
import com.example.trabalhofinal.components.MyTextField
import com.example.trabalhofinal.database.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserMainScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val ctx = LocalContext.current
    val userDao = AppDatabase.getDatabase(ctx).userDao()
    val registerUserViewModel: RegisterUserViewModel = viewModel(
        factory = RegisterUserViewModelFactory(userDao)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cadastro de Usuário") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegisterUserFields(
                registerUserViewModel = registerUserViewModel,
                onRegisterSuccess = onRegisterSuccess,
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserFields(
    registerUserViewModel: RegisterUserViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val registerUser = registerUserViewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    MyTextField(
        label = "Usuário",
        value = registerUser.value.user,
        onValueChange = { registerUserViewModel.onUserChange(it) }
    )
    MyTextField(
        label = "Nome",
        value = registerUser.value.name,
        onValueChange = { registerUserViewModel.onNameChange(it) }
    )
    MyTextField(
        label = "E-mail",
        value = registerUser.value.email,
        onValueChange = { registerUserViewModel.onEmailChange(it) }
    )
    MyPasswordField(
        label = "Senha",
        value = registerUser.value.password,
        errorMessage = registerUser.value.validatePassord(),
        onValueChange = { registerUserViewModel.onPasswordChange(it) }
    )
    MyPasswordField(
        label = "Confirmar Senha",
        value = registerUser.value.confirmPassword,
        errorMessage = registerUser.value.validateConfirmPassword(),
        onValueChange = { registerUserViewModel.onConfirmPassword(it) }
    )

    Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = {
            registerUserViewModel.register()
        }
    ) {
        Text(text = "Registrar usuário")
    }

    Button(
        modifier = Modifier.padding(top = 8.dp),
        onClick = { onNavigateToLogin() }
    ) {
        Text(text = "Voltar para o Login")
    }

    if (registerUser.value.errorMessage.isNotBlank()) {
        ErrorDialog(
            error = registerUser.value.errorMessage,
            onDismissRequest = { registerUserViewModel.cleanDisplayValues() }
        )
    }

    LaunchedEffect(registerUser.value.isSaved) {
        if (registerUser.value.isSaved) {
            Toast.makeText(ctx, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
            registerUserViewModel.cleanDisplayValues()
            onRegisterSuccess()
        }
    }
}
