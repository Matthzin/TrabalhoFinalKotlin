package com.example.trabalhofinal.screens

import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhofinal.components.ErrorDialog
import com.example.trabalhofinal.components.MyPasswordField
import com.example.trabalhofinal.components.MyTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onLoginSuccess: () -> Unit) {
    val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MyTextField(
                label = "User",
                value = loginState.user,
                onValueChange = { loginViewModel.onUserChange(it) }
            )
            MyPasswordField(
                label = "Password",
                value = loginState.password,
                onValueChange = { loginViewModel.onPasswordChange(it) }
            )

            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    if (loginViewModel.login()) {
                        Toast.makeText(ctx, "Login successful!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    }
                }
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Register",
                modifier = Modifier.padding(top = 8.dp),
                color = androidx.compose.ui.graphics.Color.Blue
            )
        }
    }

    if (loginState.errorMessage.isNotBlank()) {
        ErrorDialog(
            error = loginState.errorMessage,
            onDismissRequest = { loginViewModel.clearError() }
        )
    }
}