package com.example.sample.blesampleapplication.view.pairing

import android.nfc.Tag
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softnet.module.blemodule.amoband.AmoOmega
import com.softnet.module.blemodule.amoband.model.ScanDeviceVo
import com.softnet.module.blemodule.ble.enumeration.CheckStatus
import com.softnet.module.blemodule.ble.enumeration.CheckStatus.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PairingViewModel @Inject constructor(private val amoOmega: AmoOmega) : ViewModel() {

    private val _uiState = MutableStateFlow<PairingUIState>(PairingUIState.Idle())
    val uiState: StateFlow<PairingUIState> = _uiState

    private val pairingIntent: Channel<PairingIntent> = Channel()

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
                .collect{ intent ->
                    when(intent) {
                        PairingIntent.CheckBLEEnable -> checkBLEEnable()
                        PairingIntent.RequestPermission -> requestPermission()
                        is PairingIntent.OnPermissionResult -> onPermissionResult(intent.isGranted)
                        PairingIntent.StartScan -> startScan()
                        PairingIntent.StopScan -> stopScan()
                        PairingIntent.DismissAlert -> _uiState.value = PairingUIState.Idle()
                    }
                }
        }
    }

    private fun checkBLEEnable() {
        when(val checkStatus = amoOmega.checkBleSupport()) {
            UNAUTHORIZED -> requestPermission()
            BLUETOOTH_ENABLE, BLUETOOTH_ON -> {
                _uiState.value = PairingUIState.RequestPermission
            }
            else -> {
                _uiState.value = PairingUIState.ScanFail(checkStatus.text())
            }
        }
    }

    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            _uiState.value = PairingUIState.RequestPermission
        } else {
            _uiState.value = PairingUIState.PermissionDenied
        }
    }

    private fun onPermissionResult(isGranted: Boolean) {
        if(isGranted) {
            startScan()
        } else {
            _uiState.value = PairingUIState.PermissionDenied
        }
    }

    private fun startScan() {
        //todo: check ble enable
        _uiState.value = PairingUIState.Scanning()
        //todo: startScan
    }

    private fun stopScan() {
        _uiState.value = PairingUIState.Idle()
        //todo: stopScan
    }
}

sealed class PairingUIState {
    data class Idle(
        val buttonText: String = "스캔하기",
        val pairingIntent: PairingIntent = PairingIntent.CheckBLEEnable,
        val showProgressBar: Boolean = false
    ): PairingUIState()

    data class Scanning(
        val buttonText: String = "스캔중지",
        val pairingIntent: PairingIntent = PairingIntent.StopScan,
        val showProgressBar: Boolean = true
    ): PairingUIState()

    object RequestPermission: PairingUIState()

    object PermissionDenied: PairingUIState()

    data class ScanFail(val message: String): PairingUIState()
    data class ScanResult(val result: List<ScanDeviceVo>): PairingUIState()
}

sealed class PairingIntent {
    object CheckBLEEnable: PairingIntent()
    object RequestPermission: PairingIntent()
    data class OnPermissionResult(val isGranted: Boolean): PairingIntent()
    object StartScan: PairingIntent()

    object StopScan: PairingIntent()

    object DismissAlert: PairingIntent()
}
