package com.example.sample.blesampleapplication.view

import android.app.Activity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.sample.blesampleapplication.navigation.NavContainer
import com.example.sample.blesampleapplication.navigation.RouteAction
import com.example.sample.blesampleapplication.ui.component.FooterNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleSampleApp() {
    val navController = rememberNavController()
    val routeAction = remember(navController) { RouteAction(navController) }
    val activity = LocalContext.current as Activity
    val configuration = LocalConfiguration.current


    Scaffold(bottomBar = { FooterNavigationBar(navController = navController, activity = activity, configuration = configuration) }) {
        NavContainer(innerPadding = it, navHostController = navController)
    }
}