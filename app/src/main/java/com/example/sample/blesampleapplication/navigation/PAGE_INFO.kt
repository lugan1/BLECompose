package com.example.sample.blesampleapplication.navigation

import com.example.sample.blesampleapplication.R


sealed class PAGE_INFO(val route: String, val iconRes: Int, val title: String) {
    object Home: PAGE_INFO(route = "home", iconRes = R.drawable.ic_home, title = "홈")
    object Measure: PAGE_INFO(route = "measure", iconRes = R.drawable.ic_bluetooth_scan, title = "체온 측정")
    object Pairing: PAGE_INFO(route = "pairing", iconRes = R.drawable.ic_watch,title = "밴드 연동")

    companion object {
        val pages = listOf(Home, Measure, Pairing)
    }
}