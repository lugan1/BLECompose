package com.example.sample.blesampleapplication.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.sample.blesampleapplication.R
import com.example.sample.blesampleapplication.navigation.PAGE_INFO

@Composable
fun FooterNavigationBar(navController: NavController) {
    val pages = listOf(PAGE_INFO.Home, PAGE_INFO.Measure, PAGE_INFO.Pairing)
    var selectedItemIndex by remember { mutableStateOf(0) }

    NavigationBar {
        pages.forEachIndexed { index, page ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = page.icon), contentDescription = page.title) },
                label = { Text(page.title) },
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navController.navigate(page.route) {
                        //그래프의 시작 지점까지 팝업하여, 사용자가 항목을 선택함에 따라 백 스택에 대량의 항목이 쌓이지 않도록 방지한다.
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }

                        //동일한 항목을 다시 선택할 때, 동일한 목적지의 새 인스턴스를 생성하지 않고, 현재 목적지를 다시 시작한다. (예를 들어, 홈 버튼)
                        launchSingleTop = true

                        //이전에 선택했던 항목을 다시 선택할 때, 해당 항목의 상태를 복원한다. (예를 들어, 스크롤 위치)
                        restoreState = true
                    }
                })
        }
    }
}