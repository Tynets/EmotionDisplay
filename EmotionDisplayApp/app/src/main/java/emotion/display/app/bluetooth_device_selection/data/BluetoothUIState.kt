package emotion.display.app.bluetooth_device_selection.data

data class BluetoothUIState(
    val discoveredDevices: List<BluetoothDeviceDO> = emptyList(),
    val pairedDevices: List<BluetoothDeviceDO> = emptyList(),
    val toStartProgressBar: Boolean = false,
    val isConnected: Boolean = false,
)