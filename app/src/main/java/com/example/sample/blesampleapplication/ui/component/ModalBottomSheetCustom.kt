package com.example.sample.blesampleapplication.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sample.blesampleapplication.view.pairing.PairingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanListBottomSheet(onDismissRequest: () -> Unit, modifier: Modifier = Modifier, paringViewModel: PairingViewModel) {
    ModalBottomSheet(onDismissRequest = onDismissRequest, modifier = modifier) {

    }
}

@Preview
@Composable
fun ScanListBottomSheetPrewview() {
    ScanListBottomSheet(onDismissRequest = {}, paringViewModel = viewModel())
}
