package com.example.sample.blesampleapplication.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.sample.blesampleapplication.util.DataStoreKey
import com.example.sample.blesampleapplication.util.readStringFromDataStore

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        val macAddress = LocalContext.current.readStringFromDataStore(DataStoreKey.MAC_ADDRESS).collectAsState(initial = Unit)

        if(macAddress.value == Unit) {
            //todo: 얼럿 다이얼로그 출력
        }

        Text(text = "홈")
    }
}