package emotion.display.app.bluetooth_device_selection.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import emotion.display.app.bluetooth_device_selection.data.BluetoothDeviceDO
import emotion.display.app.bluetooth_device_selection.data.Message
import emotion.display.app.bluetooth_device_selection.data.toBluetoothDeviceDO
import emotion.display.app.bluetooth_device_selection.data.toByteArray
import emotion.display.app.bluetooth_device_selection.data.toUnsignedInt
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class BluetoothController private constructor(private val context: Context){

    companion object {
        @Volatile
        private var instance: BluetoothController? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: BluetoothController(context).also { instance = it }
            }
    }

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

    private val _message: MutableSharedFlow<String> = MutableSharedFlow()
    val message: SharedFlow<String> get() = this._message.asSharedFlow()

    private var stopListening: Boolean = false

    private val receiver: BluetoothDeviceReceiver = BluetoothDeviceReceiver { device ->
        this._discoveredDevices.update { devices ->
            when (device) {
                in this._pairedDevices.value -> devices
                !in devices -> devices + device
                else -> devices
            }
        }
    }

    private val connectionReceiver: BluetoothConnectionReceiver = BluetoothConnectionReceiver { isConnected, device ->
        try {
            if (bluetoothAdapter?.bondedDevices?.contains(device) == true) _isConnected.update { isConnected }
            // else CoroutineScope(Dispatchers.Default).launch { _message.emit("Enter pairing code") }
        } catch (e: SecurityException) {
            CoroutineScope(Dispatchers.Default).launch { Log.e("Controller", e.toString()) }
        }
    }

    private var clientSocket: BluetoothSocket? = null

    private val tag: String = "BluetoothController"

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
        this.bluetoothAdapter?.bondedDevices?.map { it.toBluetoothDeviceDO() }
            ?.let { devices -> this._pairedDevices.update { devices } }
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
        CoroutineScope(Dispatchers.Default).launch { _message.emit("Trying to connect") }
        val remoteDevice = bluetoothAdapter?.getRemoteDevice(device.address)
        this.clientSocket = remoteDevice?.createRfcommSocketToServiceRecord(serviceUUID)
        try {
            // Execute BEFORE updating the list of devices
            this.clientSocket?.connect()
            if (device !in this._pairedDevices.value) {
                this.updatePairedDevices()
                // Execute AFTER connecting to the device
                this._discoveredDevices.update { devices -> devices - device }
                Log.d(tag, "Connected to $device")
            }
            CoroutineScope(Dispatchers.Default).launch { _message.emit("Connected") }
        }
        catch (e: IOException) {
            this.disconnectFromDevice()
            CoroutineScope(Dispatchers.Default).launch { _message.emit("Cannot connect") }
            Log.e(tag, "$e")
        }
        return
    }

    fun disconnectFromDevice() {
        this.clientSocket?.close()
        this.clientSocket = null
        return
    }

    /**
     * Blocking function
     */
    fun sendMessage(message: Message) {
        try {
            this.clientSocket?.outputStream?.write(message.toByteArray())
        } catch (e: IOException) {
            CoroutineScope(Dispatchers.Default).launch { _message.emit("Error") }
            Log.e(tag, e.toString())
        }
        return
    }

    // command          0
    // category         1
    // position         2
    // sizeLSB          3
    // sizeMSB          4
    // pixels:
    //    positionLSB   5
    //    positionMSB   6
    //    R             7
    //    G             8
    //    B             9
    private fun receiveMessage() : Message? {
        try {
            val byte = ByteArray(1)
            val header = ByteArray(5)
            for (i in 0 ..< 5) {
                this.clientSocket?.inputStream?.read(byte)
                header[i] = byte[0]
            }
            val size: Int = (header[4].toUnsignedInt() shl 8) or (header[3]).toUnsignedInt()
            val data = ByteArray(size)
            for (i in 0..<size) {
                this.clientSocket?.inputStream?.read(byte)
                data[i] = byte[0]
            }
            return Message(header[0], header[1], header[2], header[3], header[4], data)
        } catch (e: IOException) {
            Log.d(tag, e.toString())
        }
        return null
    }

    fun startListening(): Flow<Message> {
        return flow {
            while (!stopListening) {
                if (!isConnected.value) return@flow
                val message: Message? = receiveMessage()
                message?.let { emit(it) }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun stopListening() {
        this.stopListening = true
        return
    }

}