package com.example.trabalhofinal.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import com.example.trabalhofinal.R
import com.example.trabalhofinal.viewmodel.LoginViewModel
import com.example.trabalhofinal.viewmodel.LoginViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    val factory = remember { LoginViewModelFactory(userDao) }
    val loginViewModel: LoginViewModel = viewModel(factory = factory)

    val loginState by loginViewModel.uiState.collectAsState()

    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.travel_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 32.dp)
            )
            MyTextField(
                label = "Usuário",
                value = loginState.user,
                onValueChange = { loginViewModel.onUserChange(it) }
            )
            MyPasswordField(
                label = "Senha",
                value = loginState.password,
                onValueChange = { loginViewModel.onPasswordChange(it) }
            )

            Button(
                onClick = {
                    loginViewModel.login()
                }
            ) {
                Text(text = "Login")
            }

            Text(
                text = "Não tem uma conta? Registre-se aqui.",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable {
                        onNavigateToRegister()
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (loginState.errorMessage.isNotBlank()) {
        ErrorDialog(
            error = loginState.errorMessage,
            onDismissRequest = { loginViewModel.clearError() }
        )
    }

    LaunchedEffect(loginState.isLoggedIn) {
        if (loginState.isLoggedIn) {
            Toast.makeText(context, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
            loginViewModel.clearLoginFlag()
            onLoginSuccess()
        }
    }
}