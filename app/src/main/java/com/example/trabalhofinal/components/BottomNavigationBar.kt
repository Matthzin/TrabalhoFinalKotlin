package com.example.trabalhofinal.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
    ) {
        NavigationBarItem(
            selected = currentRoute == "mainScreen",
            onClick = {
                if (currentRoute != "mainScreen") {
                    navController.navigate("mainScreen") {
                        popUpTo("mainScreen") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") }
        )

        NavigationBarItem(
            selected = currentRoute == "registerTrip",
            onClick = {
                if (currentRoute != "registerTrip") {
                    navController.navigate("registerTrip") {
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Add, contentDescription = "Nova Viagem") },
            label = { Text("Nova Viagem") }
        )

        NavigationBarItem(
            selected = currentRoute == "travelListScreen",
            onClick = {
                if (currentRoute != "travelListScreen") {
                    navController.navigate("travelListScreen") {
                        popUpTo("travelListScreen") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Default.List, contentDescription = "Viagens") },
            label = { Text("Minhas Viagens") }
        )
    }
}
