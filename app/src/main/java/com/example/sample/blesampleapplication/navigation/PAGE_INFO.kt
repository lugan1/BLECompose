package com.example.sample.blesampleapplication.navigation

import com.example.sample.blesampleapplication.R


sealed class PAGE_INFO(val route: String, val icon: Int, val title: String) {
    object Home: PAGE_INFO(route = "home", icon = R.drawable.ic_home, title = "홈")
    object Pairing: PAGE_INFO(route = "pairing", icon = R.drawable.ic_watch ,title = "밴드 연동")
    object Measure: PAGE_INFO(route = "measure", icon = R.drawable.ic_bluetooth_scan, title = "체온 측정")
}

/*
enum class PAGE_INFO(val route: String, val title: String) {
    Home(route = "home", "홈"),
    Pairing(route = "pairing", "밴드 연동"),
    Measure(route = "measure", "체온 측정")
}*/
