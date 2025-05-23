package com.example.trabalhofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trabalhofinal.screens.LoginScreen
import com.example.trabalhofinal.screens.MainScreen
import com.example.trabalhofinal.screens.RegisterTripScreen
import com.example.trabalhofinal.screens.RegisterUserMainScreen
import com.example.trabalhofinal.ui.theme.TrabalhoFinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrabalhoFinalTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "loginScreen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("loginScreen") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("mainScreen") {
                                        popUpTo("loginScreen") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("registerUserScreen")
                                }
                            )
                        }

                        composable("mainScreen") {
                            MainScreen(
                                onLogout = {
                                    navController.navigate("loginScreen") {
                                        popUpTo("mainScreen") { inclusive = true }
                                    }
                                },
                                onNavigateToRegisterTrip = {
                                    navController.navigate("registerTrip")
                                }
                            )
                        }

                        composable("registerUserScreen") {
                            RegisterUserMainScreen(
                                onRegisterSuccess = {
                                    navController.navigate("loginScreen") {
                                        popUpTo("registerUserScreen") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate("loginScreen") {
                                        popUpTo("registerUserScreen") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("registerTrip") {
                            RegisterTripScreen(
                                onRegisterSuccess = {
                                    navController.navigate("mainScreen") {
                                        popUpTo("registerTrip") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
