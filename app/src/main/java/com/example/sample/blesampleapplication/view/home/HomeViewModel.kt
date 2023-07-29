package com.example.sample.blesampleapplication.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.blesampleapplication.data.DataStore
import com.example.sample.blesampleapplication.data.DataStoreKey
import com.example.sample.blesampleapplication.navigation.PAGE_INFO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(dataStore: DataStore) : ViewModel() {
    private val macAddress = dataStore.readString(DataStoreKey.MAC_ADDRESS)

    private val _homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.Idle)
    val homeState: StateFlow<HomeState> = _homeState

    private val homeIntent: Channel<HomeIntent> = Channel()

    init {
        handleIntent()
        loadMacAddress()
    }

    fun sendIntent(intent: HomeIntent) = viewModelScope.launch(Dispatchers.IO) { homeIntent.send(intent) }

    private fun handleIntent() {
        viewModelScope.launch {
            homeIntent.consumeAsFlow()
                .collect{ intent ->
                    when(intent) {
                        is HomeIntent.LoadMacAddress -> loadMacAddress()
                        is HomeIntent.DismissAlert -> _homeState.value = HomeState.Idle
                    }
                }
        }
    }

    private fun loadMacAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            macAddress.collect{ mac ->
                if (mac.isEmpty()) {
                    _homeState.value = HomeState.MacAddressEmpty
                }
                else {
                    _homeState.value = HomeState.Success(mac)
                }
            }
        }
    }
}

sealed class HomeState {
    object Idle: HomeState()
    data class Success(val macAddress: String): HomeState()
    object MacAddressEmpty: HomeState()
}

sealed class HomeIntent {
    object LoadMacAddress: HomeIntent()
    object DismissAlert: HomeIntent()
}
