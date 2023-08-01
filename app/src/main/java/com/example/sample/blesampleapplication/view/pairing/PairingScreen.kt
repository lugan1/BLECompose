package com.example.sample.blesampleapplication.view.pairing

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.example.sample.blesampleapplication.navigation.PAGE_INFO
import com.example.sample.blesampleapplication.ui.component.Alert
import com.example.sample.blesampleapplication.ui.component.BluetoothPermissionTextProvider
import com.example.sample.blesampleapplication.ui.component.PermissionDialog
import com.example.sample.blesampleapplication.ui.component.TopBar
import com.example.sample.blesampleapplication.ui.component.openAppSettings

@Composable
fun PairingScreen(
    state: PairingUIState,
    sendIntent: (PairingIntent) -> Unit,
) {
    Scaffold(topBar = { TopBar(title = PAGE_INFO.Pairing.title) }) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            var buttonText by remember { mutableStateOf("스캔하기") }
            var pairingIntent: PairingIntent by remember { mutableStateOf(PairingIntent.StartScan) }
            var showProgressBar by remember { mutableStateOf(false) }

            when(state) {
                is PairingUIState.Idle -> {
                    buttonText = state.buttonText
                    pairingIntent = state.pairingIntent
                    showProgressBar = state.showProgressBar
                }
                PairingUIState.RequestPermission -> {
                    LaunchBluetoothPermissionActivity(sendIntent)
                }
                PairingUIState.PermissionDenied -> {
                    ShowPermissionDeniedAlert(sendIntent)
                }
                is PairingUIState.ScanFail -> {
                    ShowAlert(message = state.message, sendIntent = sendIntent)
                }
                is PairingUIState.Scanning -> {
                    buttonText = state.buttonText
                    pairingIntent = state.pairingIntent
                    showProgressBar = state.showProgressBar
                }
                is PairingUIState.ScanResult -> {
                    TODO()
                }
            }

            PairingTopContainer(
                buttonText = buttonText,
                onClick = { sendIntent(pairingIntent) },
                showProgressBar = showProgressBar
            )

            ScanResultColumn()
        }
    }
}

@Composable
fun ShowAlert(
    message: String,
    sendIntent: (PairingIntent) -> Unit,
) {
    val dismiss = { sendIntent(PairingIntent.DismissAlert) }
    Alert(
        titleText = "스캔 실패",
        bodyText = message,
        confirmHandle = dismiss,
        onDismissRequest = dismiss
    )
}

@Composable
fun ShowPermissionDeniedAlert(
    sendIntent: (PairingIntent) -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    PermissionDialog(
        permissionTextProvider = BluetoothPermissionTextProvider(),
        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(activity, android.Manifest.permission.BLUETOOTH),
        onDismiss = { sendIntent(PairingIntent.DismissAlert) },
        onConfirm = { sendIntent(PairingIntent.DismissAlert) },
        onGoToAppSettingsClick = { activity.openAppSettings() })
}

@Composable
fun LaunchBluetoothPermissionActivity(
    sendIntent: (PairingIntent) -> Unit,
) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val bluetoothPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted -> sendIntent(PairingIntent.OnPermissionResult(isGranted)) }
        )

        SideEffect {
            bluetoothPermissionResultLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
}

@Composable
fun PairingTopContainer(
    buttonText: String,
    onClick: () -> Unit = {},
    showProgressBar: Boolean = false
) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()){
        val (scanButton, progressBar) = createRefs()

        Button(modifier = Modifier.constrainAs(scanButton) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, onClick = onClick) {
            Text(buttonText)
        }

        if(showProgressBar){
            CircularProgressIndicator(modifier = Modifier
                .size(30.dp)
                .constrainAs(progressBar) {
                    start.linkTo(scanButton.end, margin = 20.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                })
        }
    }
}

@Composable
fun ScanResultColumn() {

}


@Preview
@Composable
fun PairingScreenPreview() {
    PairingScreen(state = PairingUIState.Idle(), sendIntent = {})
}

@Preview
@Composable
fun PairingTopContainerPreview() {
    PairingTopContainer(buttonText = "스캔하기", onClick = {}, showProgressBar = true)
}

@Preview
@Composable
fun ScanResultColumnPreview() {
    ScanResultColumn()
}