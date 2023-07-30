package com.example.sample.blesampleapplication.view.pairing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sample.blesampleapplication.navigation.PAGE_INFO
import com.example.sample.blesampleapplication.ui.component.ScanListBottomSheet
import com.example.sample.blesampleapplication.ui.component.TopBar

@Composable
fun PairingScreen(paringViewModel: PairingViewModel = hiltViewModel()) {
    Scaffold(topBar = { TopBar(title = PAGE_INFO.Pairing.title) }) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            var showBottomSheet by remember { mutableStateOf(false) }

            if(showBottomSheet) {
                ScanListBottomSheet(onDismissRequest = { showBottomSheet = false }, paringViewModel = paringViewModel)
            }

            Spacer(Modifier.height(30.dp))
            Button(onClick = { showBottomSheet = true }) {
                Text("스캔하기")
            }
        }
    }
}



@Preview
@Composable
fun PairingScreenPreview() {
    PairingScreen()
}
