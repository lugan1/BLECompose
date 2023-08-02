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
import androidx.compose.runtime.DisposableEffect
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
import com.example.sample.blesampleapplication.ui.component.LoadingDialog
import com.example.sample.blesampleapplication.ui.component.PermissionDialog
import com.example.sample.blesampleapplication.ui.component.TopBar
import com.example.sample.blesampleapplication.ui.component.openAppSettings
import com.softnet.module.blemodule.amoband.model.AdvData
import com.softnet.module.blemodule.amoband.model.ScanDeviceVo
import com.softnet.module.blemodule.util.ByteUtil

@Composable
fun PairingScreen(
    state: PairUiState,
    sendIntent: (PairingIntent) -> Unit,
    completeNavigateTo: () -> Unit,
) {
    DisposableEffect(true) {
        onDispose {
            sendIntent(PairingIntent.Dispose)
        }
    }

    Scaffold(topBar = { TopBar(title = PAGE_INFO.Pairing.title) }) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            var buttonText by remember { mutableStateOf("") }
            var showProgressBar by remember { mutableStateOf(false) }
            var scanDevices by remember { mutableStateOf(listOf<ScanDeviceVo>()) }

            when(state) {
                is PairUiState.Idle -> {
                    buttonText = state.buttonText
                    showProgressBar = state.showProgressBar
                }
                PairUiState.RequestPermission -> {
                    LaunchBluetoothPermissionActivity(sendIntent)
                }
                PairUiState.PermissionDenied -> {
                    ShowPermissionDeniedAlert(sendIntent)
                }
                is PairUiState.ScanFail -> {
                    ShowAlert(
                        title = state.title,
                        message = state.message,
                        onConfirm = { sendIntent(PairingIntent.DismissAlert) },
                        onDismissRequest = { sendIntent(PairingIntent.DismissAlert) }
                    )
                }
                is PairUiState.Scanning -> {
                    buttonText = state.buttonText
                    showProgressBar = state.showProgressBar
                }
                is PairUiState.ScanResult -> {
                    val scanDeviceIndex = scanDevices.indexOfFirst { it.macAddr == state.result.macAddr }
                    scanDevices = if (scanDeviceIndex != -1) {
                        scanDevices.mapIndexed { index, device -> if (index == scanDeviceIndex) state.result else device }
                    } else {
                        scanDevices + state.result
                    }
                }
                is PairUiState.Connecting -> {
                    LoadingDialog(
                        onDismissRequest = { sendIntent(PairingIntent.DismissAlert) },
                        text = state.text
                    )
                }
                is PairUiState.ConnectingFail -> {
                    ShowAlert(
                        title = state.title,
                        message = state.message,
                        onConfirm = { sendIntent(PairingIntent.DismissAlert) },
                        onDismissRequest = { sendIntent(PairingIntent.DismissAlert) }
                    )
                }
                is PairUiState.Connected -> {
                    ShowAlert(
                        title = state.title,
                        message = state.message,
                        onConfirm = completeNavigateTo,
                        onDismissRequest = { sendIntent(PairingIntent.DismissAlert) })
                }
            }

            PairingTopContainer(
                buttonText = buttonText,
                onClick = { sendIntent(if(state is PairUiState.Idle) PairingIntent.RequestPermission else PairingIntent.StopScan) },
                showProgressBar = showProgressBar
            )

            ScanResultColumn(scanDeviceList = scanDevices, sendIntent = sendIntent)
        }
    }
}

@Composable
fun ShowAlert(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Alert(
        titleText = title,
        bodyText = message,
        confirmHandle = onConfirm,
        onDismissRequest = onDismissRequest
    )
}

@Composable
fun ShowPermissionDeniedAlert(
    sendIntent: (PairingIntent) -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    PermissionDialog(
        permissionTextProvider = BluetoothPermissionTextProvider(),
        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
            activity,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                android.Manifest.permission.BLUETOOTH_SCAN
            } else {
                android.Manifest.permission.BLUETOOTH_ADMIN
            }
        ),
        onDismiss = { sendIntent(PairingIntent.DismissAlert) },
        onConfirm = { sendIntent(PairingIntent.DismissAlert) },
        onGoToAppSettingsClick = { activity.openAppSettings() })
}

@Composable
fun LaunchBluetoothPermissionActivity(
    sendIntent: (PairingIntent) -> Unit,
) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val permissionToRequest = arrayOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        )

        val bluetoothPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                permissionToRequest.forEach { permission ->
                    val isGranted = permissions[permission] ?: false
                    sendIntent(PairingIntent.OnPermissionResult(isGranted = isGranted))
                }
            }
        )

        SideEffect {
            bluetoothPermissionResultLauncher.launch(permissionToRequest)
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
fun ScanResultColumn(
    scanDeviceList: List<ScanDeviceVo> = emptyList(),
    sendIntent: (PairingIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.Black),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(scanDeviceList) { index, scanDevice ->
            ScanResultItem(scanDevice = scanDevice, sendIntent = sendIntent)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun Data(label: String, value: String) {
    Row {
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = value)
    }
}

@Composable
fun ScanResultItem(scanDevice: ScanDeviceVo, sendIntent: (PairingIntent) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))) {

        Data(label = "MAC 주소: ", value = scanDevice.macAddr)
        Data(label = "디바이스 타입: ", value = scanDevice.advData?.deviceType.toString())
        Data(
            label = "rawData: ",
            value = if(scanDevice.packet != null) {
                ByteUtil.byteToStringHexFormat(scanDevice.packet!!)
                    .split(" ")
                    .dropLast(scanDevice.packet!!.size - AdvData.ADV_LENGTH +1)
                    .joinToString()
            } else {
                "null"
            }
        )
        Data(label = "생체정보: ", value = scanDevice.advData.toString())
        Data(label = "rssi: ", value = scanDevice.rssi.toString())

        Row {
            Spacer(modifier = Modifier
                .width(10.dp)
                .height(20.dp))
            Button(onClick = { sendIntent(PairingIntent.Connect(scanDevice.macAddr)) }) {
                Text("연결하기")
            }
        }
    }
}


@Preview
@Composable
fun PairingScreenPreview() {
    PairingScreen(state = PairUiState.Idle(), sendIntent = {}, completeNavigateTo = {})
}

@Preview
@Composable
fun PairingTopContainerPreview() {
    PairingTopContainer(buttonText = "밴드찾기", onClick = {}, showProgressBar = true)
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
    ScanResultColumn(scanDeviceList = scandevices, sendIntent = {})
}

@Preview
@Composable
fun ScanReulstItemPreview() {
    val scanDevice = ScanDeviceVo(
        macAddr = "00:00:00:00:00:00",
        name = "BDTB",
        rssi = -50,
        packet = null,
        advData = null,
    )
    
    ScanResultItem(
        scanDevice = scanDevice,
        sendIntent = {}
    )
}