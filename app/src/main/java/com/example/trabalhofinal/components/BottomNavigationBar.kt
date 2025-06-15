package com.example.trabalhofinal.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        windowInsets = androidx.compose.foundation.layout.WindowInsets(bottom = 30),
        modifier = Modifier.fillMaxWidth(),
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
            icon = {
                if (currentRoute == "mainScreen") {
                    Icon(Icons.Filled.Home, contentDescription = "Início")
                } else {
                    Icon(Icons.Outlined.Home, contentDescription = "Início")
                }
            },
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
            icon = {
                if (currentRoute == "registerTrip") {
                    Icon(Icons.Filled.Add, contentDescription = "Nova Viagem")
                } else {
                    Icon(Icons.Outlined.Add, contentDescription = "Nova Viagem")
                }
            },
            label = { Text("Nova Viagem") }
        )

        NavigationBarItem(
            selected = currentRoute == "aboutScreen",
            onClick = {
                if (currentRoute != "aboutScreen") {
                    navController.navigate("aboutScreen") {
                        popUpTo("aboutScreen") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                if (currentRoute == "aboutScreen") {
                    Icon(Icons.Filled.Info, contentDescription = "Sobre")
                } else {
                    Icon(Icons.Outlined.Info, contentDescription = "Sobre")
                }
            },
            label = { Text("Sobre") }
        )
    }
}

