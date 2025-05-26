package com.example.myapplication.bluetooth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BluetoothUIState(
    val discoveredDevices: List<BluetoothDeviceDO> = emptyList(),
    val pairedDevices: List<BluetoothDeviceDO> = emptyList(),
    val isConnected: Boolean = false,
    val message: String? = null,
)