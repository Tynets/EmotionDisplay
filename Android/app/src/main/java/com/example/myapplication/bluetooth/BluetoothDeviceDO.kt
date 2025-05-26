package com.example.myapplication.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

data class BluetoothDeviceDO(
    val name: String?,
    val address: String
)

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDO(): BluetoothDeviceDO {
    return BluetoothDeviceDO(name = name, address = address)
}