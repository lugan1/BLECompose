package com.example.sample.blesampleapplication.view.measure

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sample.blesampleapplication.navigation.PAGE_INFO
import com.example.sample.blesampleapplication.ui.component.TopBar

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasureScreen() {
    Scaffold(topBar = { TopBar(title = PAGE_INFO.Measure.title) }) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
        }
    }
}

@Composable
fun MeasureChart() {

}