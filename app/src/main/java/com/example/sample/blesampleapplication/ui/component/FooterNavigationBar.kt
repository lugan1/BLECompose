package com.example.sample.blesampleapplication.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sample.blesampleapplication.navigation.PAGE_INFO


@Composable
fun FooterNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        PAGE_INFO.pages.forEach { page ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = page.iconRes), contentDescription = page.title) },
                label = { Text(page.title) },
                selected = currentDestination?.hierarchy?.any { it.route == page.route } == true,
                onClick = {
                    navController.navigate(page.route) {
                        //그래프의 시작 지점까지 팝업하여, 사용자가 항목을 선택함에 따라 백 스택에 대량의 항목이 쌓이지 않도록 방지한다.
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }

                        //동일한 항목을 다시 선택할 때, 동일한 목적지의 새 인스턴스를 생성하지 않고, 현재 목적지를 다시 시작한다. (예를 들어, 홈 버튼)
                        launchSingleTop = true
                    }
                })
        }
    }
}