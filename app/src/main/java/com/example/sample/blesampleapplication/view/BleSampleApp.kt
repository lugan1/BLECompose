package com.example.sample.blesampleapplication.view

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.sample.blesampleapplication.navigation.NavContainer
import com.example.sample.blesampleapplication.ui.component.FooterNavigationBar


@Composable
fun BleSampleApp() {
    val navController = rememberNavController()

    Scaffold(bottomBar = { FooterNavigationBar(navController = navController) }) {
        NavContainer(innerPadding = it, navHostController = navController)
    }
}
