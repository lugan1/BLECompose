package com.example.sample.blesampleapplication.view.pairing

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
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
import com.softnet.module.blemodule.amoband.model.ScanDeviceVo

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
            var scanDevices by remember { mutableStateOf(listOf<ScanDeviceVo>()) }

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
                    val scanDeviceIndex = scanDevices.indexOfFirst { it.macAddr == state.result.macAddr }
                    scanDevices = if (scanDeviceIndex != -1) {
                        scanDevices.mapIndexed { index, device ->
                            if (index == scanDeviceIndex) state.result else device
                        }
                    } else {
                        scanDevices + state.result
                    }
                }
            }

            PairingTopContainer(
                buttonText = buttonText,
                onClick = { sendIntent(pairingIntent) },
                showProgressBar = showProgressBar
            )

            ScanResultColumn(scanDevices)
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
        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(activity, android.Manifest.permission.BLUETOOTH_SCAN),
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
            bluetoothPermissionResultLauncher.launch(android.Manifest.permission.BLUETOOTH_SCAN)
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
fun ScanResultColumn(scanDeviceList: List<ScanDeviceVo> = emptyList()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.Black),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(scanDeviceList) { index, scanDevice ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))) {

                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "MAC 주소: ")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = scanDevice.macAddr)
                }

                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "체온: ")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = scanDevice.advData?.temperature.toString())
                }

                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "rssi: ")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = scanDevice.rssi.toString())
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
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
    val scandevices: List<ScanDeviceVo> = List(10) {
        ScanDeviceVo(
            macAddr = "00:00:00:00:00:00",
            name = "BDTB",
            rssi = -50,
            packet = null,
            advData = null,
        )
    }
    ScanResultColumn(scanDeviceList = scandevices)
}