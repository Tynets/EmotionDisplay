package com.example.myapplication.data

data class BluetoothUIState(
    val discoveredDevices: List<BluetoothDeviceDO> = emptyList(),
    val pairedDevices: List<BluetoothDeviceDO> = emptyList(),
    val toStartProgressBar: Boolean = false,
    val isConnected: Boolean = false,
)