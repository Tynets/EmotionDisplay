package com.example.myapplication.bluetooth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BluetoothViewModel(private val application: Application) : AndroidViewModel(application) {

    private var bluetoothController: BluetoothController = BluetoothController(this.application.applicationContext)

    private val _uiState : MutableStateFlow<BluetoothUIState> = MutableStateFlow(BluetoothUIState())
    val uiState: StateFlow<BluetoothUIState> = combine(
        this.bluetoothController.discoveredDevices,
        this.bluetoothController.pairedDevices,
        this._uiState
    ) { discoveredDevices, pairedDevices, uiState ->
            uiState.copy(
                discoveredDevices = discoveredDevices,
                pairedDevices = pairedDevices,
                message = if(uiState.isConnected) uiState.message else null
            )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), this._uiState.value)

    private var connectToDeviceJob: Job? = null

    init {
        this.bluetoothController.isConnected.onEach { isConnected ->
            this._uiState.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)
        this.bluetoothController.messages.onEach { message ->
            this._uiState.update { it.copy(message = message) }
        }.launchIn(viewModelScope)
    }

    fun startDiscovery() {
        this.bluetoothController.startDiscovery()
        return
    }

    fun cancelDiscovery() {
        this.bluetoothController.cancelDiscovery()
        return
    }

    fun connectToDevice(device: BluetoothDeviceDO) {
        this._uiState.update { it.copy(isConnected = true) }
        this.connectToDeviceJob = CoroutineScope(Dispatchers.IO).launch {
            bluetoothController.connectToDevice(device)
        }
        return
    }

    fun disconnectFromDevice() {
        this._uiState.update { it.copy(isConnected = false) }
        this.connectToDeviceJob?.cancel()
        this.bluetoothController.disconnectFromDevice()
        return
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch { bluetoothController.sendMessage(message) }
        return
    }

    //

    override fun onCleared() {
        super.onCleared()
        this.bluetoothController.unregisterAll()
        return
    }

}