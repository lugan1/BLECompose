package com.example.sample.blesampleapplication.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sample.blesampleapplication.view.pairing.PairingScreen


@Composable
fun NavContainer(innerPadding: PaddingValues, navHostController: NavHostController) {
    NavHost(modifier = Modifier.padding(innerPadding), navController = navHostController, startDestination = PAGE_INFO.ParingScreen.route) {
        composable(PAGE_INFO.ParingScreen.route) { PairingScreen() }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navItems = listOf("1", "2", "3")
    val selected by remember { mutableStateOf("1") }

    NavigationBar {
        navItems.forEach { navItem ->
            NavigationBarItem(
                icon = {},
                selected = navItem == selected,
                label = {},
                onClick = { navController.navigate(navItem) }
            )
        }
    }
}

// 네비게이션 라우트 하는 기능을 모음한 클래스
class RouteAction(val navController: NavHostController) {

    // 특정 페이지 이동
    val navTo: (PAGE_INFO) -> Unit = { page ->
        navController.navigate(page.route)
    }

    // 뒤로가기 이동
    val goBack: () -> Unit = {
        navController.navigateUp()
    }

}