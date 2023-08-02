package com.example.sample.blesampleapplication.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sample.blesampleapplication.view.home.HomeScreen
import com.example.sample.blesampleapplication.view.home.HomeViewModel
import com.example.sample.blesampleapplication.view.measure.MeasureScreen
import com.example.sample.blesampleapplication.view.pairing.PairingScreen
import com.example.sample.blesampleapplication.view.pairing.PairingViewModel


@Composable
fun NavContainer(innerPadding: PaddingValues, navHostController: NavHostController ) {
    RouteEventHandle(navHostController = navHostController)
    NavHost(modifier = Modifier.padding(innerPadding), navController = navHostController, startDestination = PAGE_INFO.Home.route) {
        composable(PAGE_INFO.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.homeState.collectAsState()
            HomeScreen(navController = navHostController, state = state, sendIntent = viewModel::sendIntent)
        }
        composable(PAGE_INFO.Measure.route) { MeasureScreen() }
        composable(PAGE_INFO.Pairing.route) {
            val viewModel: PairingViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            PairingScreen(state = state, sendIntent = viewModel::sendIntent, completeNavigateTo = { navHostController.navigate(PAGE_INFO.Home.route) })
        }
    }
}

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun RouteEventHandle(
    navHostController: NavHostController
) {
    val activity = LocalContext.current as Activity
    val configuration = LocalConfiguration.current

    LaunchedEffect(key1 = navHostController) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("navigation", "destination $destination")

            if(destination.route == PAGE_INFO.Measure.route && configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            else if(destination.route != PAGE_INFO.Measure.route && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}

// 네비게이션 라우트 하는 기능을 모음한 클래스
class RouteAction(private val navController: NavHostController) {

    // 특정 페이지 이동
    val navTo: (PAGE_INFO) -> Unit = { page ->
        navController.navigate(page.route)
    }

    // 뒤로가기 이동
    val goBack: () -> Unit = {
        navController.navigateUp()
    }

}