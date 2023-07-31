package com.example.sample.blesampleapplication.view.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softnet.module.blemodule.amoband.AmoOmega
import com.softnet.module.blemodule.amoband.model.ScanDeviceVo
import com.softnet.module.blemodule.ble.enumeration.CheckStatus
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

    private val _uiState = MutableStateFlow<PairingUIState>(PairingUIState.Idle)
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
                         PairingIntent.StartScan -> {
                             val checkStatus = amoOmega.checkBleSupport()
                             if(checkStatus != CheckStatus.BLUETOOTH_ENABLE) {
                                 _uiState.value = PairingUIState.ScanFail(checkStatus.text())
                                 return@collect
                             }

                             startScan()
                         }
                         PairingIntent.StopScan -> stopScan()
                         PairingIntent.DismissAlert -> _uiState.value = PairingUIState.Idle
                    }
                }
        }
    }

    private fun requestPermission() {

    }

    private fun startScan() {
        //todo: check ble enable
        _uiState.value = PairingUIState.Scanning
        //todo: startScan
    }

    private fun stopScan() {
        _uiState.value = PairingUIState.Idle
        //todo: stopScan
    }
}


sealed class PairingUIState {
    object Idle: PairingUIState()

    object Scanning: PairingUIState()

    data class ScanFail(val message: String): PairingUIState()
    data class ScanResult(val result: List<ScanDeviceVo>): PairingUIState()
}

sealed class PairingIntent {

    object StartScan: PairingIntent()

    object StopScan: PairingIntent()

    object DismissAlert: PairingIntent()
}
