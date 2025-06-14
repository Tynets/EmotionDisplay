package emotion.display.app.bluetooth_device_selection.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import emotion.display.app.bluetooth_device_selection.data.BluetoothDeviceDO
import emotion.display.app.bluetooth_device_selection.data.toBluetoothDeviceDO

class BluetoothDeviceReceiver() : BroadcastReceiver() {

    private lateinit var onDeviceFound: (BluetoothDeviceDO) -> Unit

    private val tag: String = "BluetoothDeviceReceiver"

    constructor(onDeviceFound: (BluetoothDeviceDO) -> Unit) : this() {
        this.onDeviceFound = onDeviceFound
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
        when (action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (context!!.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED ) return
                //if (device?.name?.contains("HC-05") == true)
                if (device?.name == null) return
                device.toBluetoothDeviceDO().let(onDeviceFound)
                Log.d(tag, "Found: ${device.name}")
            }
        }
        return
    }

}