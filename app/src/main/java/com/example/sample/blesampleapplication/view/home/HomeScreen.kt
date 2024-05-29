package com.example.sample.blesampleapplication.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sample.blesampleapplication.navigation.PAGE_INFO
import com.example.sample.blesampleapplication.ui.component.ConfirmAlert


@Composable
fun HomeScreen(
    navController: NavController,
    state: HomeState,
    sendIntent: (HomeIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        var macAddress by remember { mutableStateOf("연동필요") }

        LaunchedEffect(true) {
            sendIntent(HomeIntent.LoadMacAddress)
        }

        when(state) {
            is HomeState.Success -> {
                macAddress = state.macAddress
            }

            HomeState.MacAddressEmpty -> {
                ConfirmAlert(
                    titleText = "밴드 연동확인",
                    bodyText = "밴드가 페어링되어있지 않습니다.\n 밴드 연동페이지로 이동하시겠습니까?",
                    confirmHandle = { navController.navigate(PAGE_INFO.Pairing.route) },
                    onDismissRequest = { sendIntent(HomeIntent.DismissAlert) },
                    dismissHandle = { sendIntent(HomeIntent.DismissAlert) }
                )
            }
            HomeState.Idle -> {}
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {

            Text("연동된 밴드: ")
            Spacer(Modifier.width(2.dp))
            Box(modifier = Modifier
                .width(LocalConfiguration.current.screenWidthDp.dp / 3)
                .height(30.dp)
                .background(Color.LightGray)
                .border(width = 1.dp, color = Color.Gray, shape = RectangleShape)) {
                Text(modifier = Modifier.align(Alignment.Center), text = macAddress)
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navController = rememberNavController(),
        state = HomeState.Success("00:00:00:00:00:00"),
        sendIntent = {  }
    )
}
