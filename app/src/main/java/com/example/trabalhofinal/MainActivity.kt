package com.example.trabalhofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trabalhofinal.screens.*
import com.example.trabalhofinal.ui.theme.TrabalhoFinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrabalhoFinalTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0)
                ) { innerPadding ->
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
                                },
                                navController = navController
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
                                onRegisterTripSuccess = {
                                    navController.navigate("mainScreen") {
                                        popUpTo("mainScreen") { inclusive = true }
                                    }
                                },
                                navController = navController,
                                onBack = {
                                    navController.navigate("mainScreen")
                                }
                            )
                        }

                        composable("aboutScreen") {
                            AboutScreen(
                                navController = navController,
                                onBack = {
                                    navController.navigate("mainScreen") {
                                        popUpTo("aboutScreen") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            "editTrip/{tripId}",
                            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getInt("tripId")
                            EditTripScreen(
                                tripId = tripId,
                                navController = navController,
                                onTripUpdated = {
                                    navController.popBackStack()
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
