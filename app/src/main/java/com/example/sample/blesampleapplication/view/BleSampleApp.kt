package com.example.sample.blesampleapplication.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.sample.blesampleapplication.navigation.NavContainer
import com.example.sample.blesampleapplication.navigation.RouteAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleSampleApp() {
    val navController = rememberNavController()
    val routeAction = remember(navController) { RouteAction(navController) }

    Scaffold {
        NavContainer(innerPadding = it, navHostController = navController)
    }
}