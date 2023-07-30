package com.example.sample.blesampleapplication.view.pairing

import androidx.lifecycle.ViewModel
import com.softnet.module.blemodule.amoband.AmoOmega
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PairingViewModel @Inject constructor(amoOmega: AmoOmega) : ViewModel() {

    fun requestPermission() {
        
    }

    private fun startScan() {

    }

    private fun stopScan() {

    }
}

sealed class PairingState {
    object ShowBottomSheet: PairingState()
    object DismissBottomSheet: PairingState()
}

sealed class ParingIntent {
    object RequestPermission: ParingIntent()

    object StartScan: ParingIntent()

    object StopScan: ParingIntent()
}
