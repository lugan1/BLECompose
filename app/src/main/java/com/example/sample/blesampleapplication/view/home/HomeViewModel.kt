package com.example.sample.blesampleapplication.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.blesampleapplication.data.DataStore
import com.example.sample.blesampleapplication.data.DataStoreKey
import com.example.sample.blesampleapplication.ui.component.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(dataStore: DataStore) : ViewModel() {
    val macAddress = dataStore.readString(DataStoreKey.MAC_ADDRESS)

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.dismiss)
    val dialogState = _dialogState.asStateFlow()

    init {
        checkMacAddress()
    }

    private fun checkMacAddress() {
        viewModelScope.launch {
            macAddress
                .filter { it.isEmpty() }
                .collect {
                    showDialog(
                        title = "밴드 연동확인",
                        body = "밴드가 페어링되어있지 않습니다.\n밴드 연동페이지로 이동하시겠습니까?",
                        onConfirm = ::navToPairingScreen
                    )
                }
        }
    }

    fun navToPairingScreen() {

    }

    fun showDialog(title: String, body: String, onConfirm: () -> Unit) {
        _dialogState.value = DialogState.show(title = title, body = body, onConfirm = onConfirm, onDismiss = { dismissDialog() })
    }

    fun dismissDialog() {
        _dialogState.value = DialogState.dismiss
    }
}
