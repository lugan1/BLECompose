package com.example.sample.blesampleapplication.view.pairing

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.blesampleapplication.data.DataStore
import com.example.sample.blesampleapplication.data.DataStoreKey
import com.example.sample.blesampleapplication.util.throttleFirst
import com.softnet.module.blemodule.amoband.AmoOmega
import com.softnet.module.blemodule.amoband.AmoOmega.BLE_DATE_FORMAT
import com.softnet.module.blemodule.amoband.command.SetDevice
import com.softnet.module.blemodule.amoband.enumeration.DeviceType
import com.softnet.module.blemodule.amoband.model.ScanDeviceVo
import com.softnet.module.blemodule.ble.enumeration.CheckStatus.BLE_UNSUPPORTED
import com.softnet.module.blemodule.ble.enumeration.CheckStatus.BLUETOOTH_ENABLE
import com.softnet.module.blemodule.ble.enumeration.CheckStatus.BLUETOOTH_OFF
import com.softnet.module.blemodule.ble.enumeration.CheckStatus.BLUETOOTH_ON
import com.softnet.module.blemodule.ble.enumeration.CheckStatus.BLUETOOTH_UNSUPPORTED
import com.softnet.module.blemodule.ble.enumeration.CheckStatus.UNAUTHORIZED
import com.softnet.module.blemodule.ble.enumeration.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class PairingViewModel @Inject constructor(
    private val amoOmega: AmoOmega,
    private val dataStore: DataStore
) : ViewModel() {

    private val pairingIntent: Channel<PairingIntent> = Channel()
    private val _uiState = MutableStateFlow<PairUiState>(PairUiState.Idle())
    val uiState: StateFlow<PairUiState> = _uiState

    private val compositeDisposable = CompositeDisposable()


    init {
        handleIntent()
    }

    fun sendIntent(intent: PairingIntent)  {
        viewModelScope.launch(Dispatchers.IO) {
            pairingIntent.send(intent)
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            pairingIntent.consumeAsFlow()
                .throttleFirst(1000L)
                .collect{ intent ->
                    when(intent) {
                        PairingIntent.StartScan -> checkBLESupport()
                        is PairingIntent.OnPermissionResult -> onPermissionResult(intent.isGranted)
                        PairingIntent.StopScan -> stopScan()
                        PairingIntent.DismissAlert -> _uiState.value = PairUiState.Idle()
                        is PairingIntent.Connect ->  {
                            if(amoOmega.isScanning) {
                                stopScan()
                                delay(100L)
                            }
                            connect(intent.macAddress)
                        }

                        PairingIntent.Dispose -> compositeDisposable.dispose()
                    }
                }
        }
    }

    private fun checkBLESupport() {
        when(val checkStatus = amoOmega.checkBleSupport()) {
            UNAUTHORIZED -> requestPermission()
            BLUETOOTH_ENABLE, BLUETOOTH_ON -> startScan()
            BLUETOOTH_OFF, BLUETOOTH_UNSUPPORTED, BLE_UNSUPPORTED -> {
                _uiState.value = PairUiState.ScanFail(
                    title = "스캔실패",
                    message = checkStatus.text()
                )
            }
        }
    }

    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            _uiState.value = PairUiState.RequestPermission
        }
        else {
            _uiState.value = PairUiState.PermissionDenied
        }
    }

    private fun onPermissionResult(isGranted: Boolean) {
        if(isGranted) {
            startScan()
        }
        else {
            _uiState.value = PairUiState.PermissionDenied
        }
    }

    private fun startScan() {
        _uiState.value = PairUiState.Scanning()
        val disposable = amoOmega.startScan("")
            .subscribe(
                { scanDevice ->
                    _uiState.value = PairUiState.OnScanResult(scanDevice)
                },
                { throwable ->
                    _uiState.value = PairUiState.ScanFail(
                        title = "스캔 실패",
                        message = throwable.message ?: "스캔 실패했습니다."
                    )
                },
                {
                    _uiState.value = PairUiState.Idle()
                }
            )

        compositeDisposable.add(disposable)
    }

    private fun stopScan() {
        amoOmega.stopScan()
        _uiState.value = PairUiState.Idle()
    }

    private fun connect(macAddress: String) {
        val disposable = amoOmega.connect(macAddress, false)
            .doAfterNext { connectionState ->
                _uiState.value = PairUiState.Connecting(connectionState.text())
            }
            .filter{ connectionState -> connectionState == ConnectionState.CHARACTERISTIC_NOTIFY_ON }
            .flatMapSingle { amoOmega.deviceAllInfo }
            .flatMapSingle { deviceInfo ->
                _uiState.value = PairUiState.Connecting("시간 동기화 중")
                val now = LocalDateTime.now()
                val minute: Long = ChronoUnit.MINUTES.between(deviceInfo.deviceTime, now)
                return@flatMapSingle if (minute > 3) amoOmega.setDeviceInfo(
                    SetDevice.TIME,
                    now.format(BLE_DATE_FORMAT)
                ) else Single.just(ConnectionState.CONNECTED)
            }
            .flatMap { amoOmega.disconnect() }
            .subscribe(
                {
                    saveMacAddress(macAddress = macAddress)
                    _uiState.value = PairUiState.Connected(title = "연동완료", message = "연동이 완료되었습니다.")
                },
                { throwable -> _uiState.value =
                    PairUiState.ConnectingFail(
                        title = "연결실패",
                        message = throwable.message ?: "연결 실패했습니다."
                    )
                }
            )

        compositeDisposable.add(disposable)
    }

    private fun saveMacAddress(macAddress: String) {
        viewModelScope.launch {
            dataStore.saveString(DataStoreKey.MAC_ADDRESS, macAddress)
        }
    }
}

sealed class PairUiState {
    data class Idle(
        val buttonText: String = "밴드찾기",
        val showProgressBar: Boolean = false
    ): PairUiState()

    data class Scanning(
        val buttonText: String = "스캔중지",
        val showProgressBar: Boolean = true
    ): PairUiState()

    object RequestPermission: PairUiState()

    object PermissionDenied: PairUiState()

    data class ScanFail(val title: String, val message: String): PairUiState()
    data class OnScanResult(val result: ScanDeviceVo): PairUiState()

    data class Connecting(val text: String): PairUiState()
    data class ConnectingFail(val title: String, val message: String): PairUiState()
    data class Connected(val title: String, val message: String): PairUiState()
}

sealed class PairingIntent {
    data class OnPermissionResult(val isGranted: Boolean): PairingIntent()
    object StartScan: PairingIntent()

    object StopScan: PairingIntent()

    object DismissAlert: PairingIntent()

    data class Connect(val macAddress: String): PairingIntent()

    object Dispose: PairingIntent()
}