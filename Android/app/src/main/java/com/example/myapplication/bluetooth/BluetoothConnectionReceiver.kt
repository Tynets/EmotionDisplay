package com.example.myapplication.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothConnectionReceiver() : BroadcastReceiver() {

    private lateinit var onConnectionChanged: (Boolean, BluetoothDevice) -> Unit

    constructor(onConnectionChanged: (Boolean, BluetoothDevice) -> Unit) : this() {
        this.onConnectionChanged = onConnectionChanged
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
        val device: BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        when (action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> { onConnectionChanged(true, device?: return) }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> { onConnectionChanged(false, device?: return) }
        }
        return
    }

}