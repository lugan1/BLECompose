package com.example.sample.blesampleapplication.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sample.blesampleapplication.navigation.PAGE_INFO
import com.example.sample.blesampleapplication.ui.component.Alert
import com.example.sample.blesampleapplication.ui.component.DialogState

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        val macAddress by homeViewModel.macAddress.collectAsState(initial = "loading...")
        val dialogState by homeViewModel.dialogState.collectAsState()

        when(dialogState) {
            is DialogState.show -> {
                val currentState = dialogState as DialogState.show
                Alert(
                    titleTxt = currentState.title,
                    bodyText = currentState.body,
                    confirmHandle = {
                        currentState.onConfirm
                        navController.navigate(PAGE_INFO.Pairing.route)
                    },
                    dismissHandle = currentState.onDismiss,
                    onDismissRequest = { homeViewModel.dismissDialog() }
                )
            }
            else -> {}
        }

        Text(text = macAddress)
    }
}