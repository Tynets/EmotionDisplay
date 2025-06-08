package com.example.myapplication.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

class BluetoothDeviceReceiver(private val onDeviceFound: (BluetoothDeviceDO) -> Unit) : BroadcastReceiver() {

    constructor() : this({ })

    @UnstableApi
    @Throws(SecurityException::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
        when (action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (context!!.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED ) return
                //if (device?.name?.contains("HC-05") == true)
                if (device?.name == null) return
                device?.toBluetoothDeviceDO()?.let(onDeviceFound)
                Log.d("Receiver", "Found: ${device.name}");
            }
        }
        return
    }

}