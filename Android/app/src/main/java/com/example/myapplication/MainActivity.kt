package com.example.myapplication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.DevicesAdapter
import com.example.myapplication.bluetooth.BluetoothViewModel
import com.example.myapplication.data.Message
import com.example.myapplication.data.toStr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private val bluetoothManager: BluetoothManager? by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager?.adapter
    }

    private val requestEnableBT: Int = 1

    private val viewModel: BluetoothViewModel by viewModels()

    private lateinit var progressBar: ProgressBar

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.layout)
        val connectionStatus: TextView = this.findViewById(R.id.connectionStatus)
        this.progressBar = this.findViewById(R.id.progressBar)

        if (this.bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "No Bluetooth support", Toast.LENGTH_SHORT).show()
            return
        }
        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
            )
        }
        if (this.bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, requestEnableBT)
        }

        val pairedAdapter = DevicesAdapter { device ->
            this.viewModel.connectToDevice(device)
        }
        val pairedRecyclerView: RecyclerView = findViewById(R.id.pairedRecyclerView)
        pairedRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        pairedRecyclerView.adapter = pairedAdapter

        val discoveredAdapter = DevicesAdapter { device ->
            this.viewModel.connectToDevice(device)
        }
        val discoveredRecyclerView: RecyclerView = findViewById(R.id.discoveredRecyclerView)
        discoveredRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        discoveredRecyclerView.adapter = discoveredAdapter
        lifecycleScope.launch {
            viewModel.uiState.collect {
                pairedAdapter.updateDevices(it.pairedDevices)
                discoveredAdapter.updateDevices(it.discoveredDevices)
                connectionStatus.text = if (it.isConnected) getString(R.string.connected)
                                        else getString(R.string.notConnected)
                progressBar.visibility = if (it.toStartProgressBar) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            viewModel.messages.collect { Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                if (it == "Connected") viewModel.startListening()
            }
        }
        val eyesPath = filesDir.toString() + "/" + getString(R.string.eyesFolder)
        val mouthsPath = filesDir.toString() + "/" + getString(R.string.mouthsFolder)
        File(eyesPath).mkdirs()
        File(mouthsPath).mkdirs()
        Log.d("Main", eyesPath)
        Log.d("Main", mouthsPath)
        var mc = 0
        lifecycleScope.launch {
            viewModel.bluetoothMessages.collect {
                mc++
                Log.d("Main", "$mc: ${it.toStr()}")
                if (it.command == 'a'.code.toByte()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val path = if (it.category == 'e'.code.toByte()) eyesPath else mouthsPath
                        val file = FileOutputStream("$path/${it.position}.bin")
                        file.write(it.pixels)
                        file.close()
                    }
                }
            }
        }
        return
    }

    override fun onDestroy() {
        super.onDestroy()
        this.viewModel.stopListening()
        return
    }

    fun onStartDiscovery(v: View) {
        this.viewModel.startDiscovery()
        return
    }

    fun onSend(v: View) {
        val message = Message('a'.code.toByte())
            //, 'e'.code.toByte(), 1, 1 ,0, byteArrayOf(0, 0, 255.toSignedByte(), 0, 0))
        this.viewModel.sendMessage(message)
        Log.d("Main", "Sent ${message.toStr()}")
        return
    }

    fun onStopDiscovery(v: View) {
        this.viewModel.cancelDiscovery()
    }

}