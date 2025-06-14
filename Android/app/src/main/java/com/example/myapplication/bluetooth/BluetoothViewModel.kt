package com.example.myapplication.bluetooth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.BluetoothDeviceDO
import com.example.myapplication.data.BluetoothUIState
import com.example.myapplication.data.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BluetoothViewModel(private val application: Application) : AndroidViewModel(application) {

    private var bluetoothController: BluetoothController = BluetoothController.getInstance(this.application.applicationContext)

    private val _bluetoothMessages: MutableSharedFlow<Message> = MutableSharedFlow()
    val bluetoothMessages: SharedFlow<Message> = this._bluetoothMessages.asSharedFlow()

    val messages : SharedFlow<String> get() = this.bluetoothController.message

    private val _uiState : MutableStateFlow<BluetoothUIState> = MutableStateFlow(BluetoothUIState())
    val uiState: StateFlow<BluetoothUIState> = combine(
        this.bluetoothController.discoveredDevices,
        this.bluetoothController.pairedDevices,
        this.bluetoothController.isConnected,
        this._uiState
    ) { discoveredDevices, pairedDevices, isConnected, state ->
        state.copy(
            discoveredDevices = discoveredDevices,
            pairedDevices = pairedDevices,
            isConnected = isConnected
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), this._uiState.value)

    private var connectToDeviceJob: Job? = null

    private var listenForMessagesJob: Job? = null

    fun startDiscovery() {
        _uiState.update { it.copy(toStartProgressBar = true) }
        this.bluetoothController.startDiscovery()
        return
    }

    fun cancelDiscovery() {
        _uiState.update { it.copy(toStartProgressBar = false) }
        this.bluetoothController.cancelDiscovery()
        return
    }

    fun connectToDevice(device: BluetoothDeviceDO) {
        this.connectToDeviceJob = CoroutineScope(Dispatchers.IO).launch {
            _uiState.update { it.copy(toStartProgressBar = true) }
            bluetoothController.connectToDevice(device)
            _uiState.update { it.copy(toStartProgressBar = false) }
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

    fun startListening() {
        this.listenForMessagesJob = this.bluetoothController.startListening().listen()
        return
    }

    fun stopListening() {
        this.bluetoothController.stopListening()
        this.listenForMessagesJob?.cancel()
        return
    }

    private fun Flow<Message>.listen() : Job {
        return onEach { _bluetoothMessages.emit(it) }
            .catch {
                bluetoothController.disconnectFromDevice()
                _uiState.update { it.copy(isConnected = false) }
            }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    override fun onCleared() {
        super.onCleared()
        this.bluetoothController.unregisterAll()
        return
    }

}