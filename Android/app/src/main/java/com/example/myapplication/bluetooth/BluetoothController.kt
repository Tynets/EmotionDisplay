package com.example.myapplication.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class BluetoothController(private val context: Context) {

    private val serviceUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val bluetoothManager: BluetoothManager? by lazy {
        this.context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager?.adapter }

    private val _discoveredDevices: MutableStateFlow<List<BluetoothDeviceDO>> = MutableStateFlow(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDeviceDO>> get() = this._discoveredDevices.asStateFlow()

    private val _pairedDevices: MutableStateFlow<List<BluetoothDeviceDO>> = MutableStateFlow(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDeviceDO>> get() = this._pairedDevices.asStateFlow()

    private val _isConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> get() = _isConnected.asStateFlow()

    private val _messages: MutableSharedFlow<String> = MutableSharedFlow()
    val messages: SharedFlow<String> get() = this._messages.asSharedFlow()

    private val receiver = BluetoothDeviceReceiver { device ->
        this._discoveredDevices.update { devices ->
            if (device !in devices) devices + device else devices
        }
    }
    private val connectionReceiver = BluetoothConnectionReceiver { isConnected, device ->
        try {
            if (bluetoothAdapter?.bondedDevices?.contains(device) == true) _isConnected.update { isConnected }
            else {
                CoroutineScope(Dispatchers.Default).launch { _messages.emit("Cannot connect"); }
            }
        } catch (e: SecurityException) {
            CoroutineScope(Dispatchers.Default).launch { _messages.emit(e.toString()); }
        }
    }

    private var clientSocket: BluetoothSocket? = null

    init {
        this.updatePairedDevices()
        this.context.registerReceiver(this.receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        this.context.registerReceiver(this.connectionReceiver, IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        })
    }

    private fun updatePairedDevices() {
        if (this.context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) return
        this.bluetoothAdapter?.bondedDevices?.also { this._pairedDevices.update { it } }
        return
    }

    fun startDiscovery() {
        if (this.context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) return
        this.bluetoothAdapter?.startDiscovery()
        return
    }

    fun cancelDiscovery() {
        if (this.context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) return
        if (this.bluetoothAdapter?.isDiscovering == true) this.bluetoothAdapter?.cancelDiscovery()
        return
    }

    /**
     * Call when the object is no longer needed
     */
    fun unregisterAll() {
        this.context.unregisterReceiver(this.receiver)
        this.context.unregisterReceiver(this.connectionReceiver)
        return
    }

    /**
     * Blocking function. Call inside a coroutine or thread
     */
    fun connectToDevice(device: BluetoothDeviceDO) {
        if (this.context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) return
        cancelDiscovery()
        val device = bluetoothAdapter?.getRemoteDevice(device.address)
        this.clientSocket = device?.createRfcommSocketToServiceRecord(serviceUUID)
        try {
            this.clientSocket?.connect()
        }
        catch (e: IOException) {
            this.disconnectFromDevice()
        }
        return
    }

    fun disconnectFromDevice() {
        this.clientSocket?.close()
        this.clientSocket = null
        return
    }

    fun sendMessage(message: Message) {
        this.clientSocket?.outputStream?.write(message.toByteArray())
        return
    }

}