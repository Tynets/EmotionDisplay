package com.example.myapplication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.bluetooth.BluetoothViewModel
import com.example.myapplication.bluetooth.DevicesAdapter
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val bluetoothManager: BluetoothManager? by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager?.adapter
    }

    private val requestEnableBT: Int = 1

    private val viewModel: BluetoothViewModel by viewModels()

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        /*
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Ryba",
                        ryba = "Sasiska",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        */
        setContentView(R.layout.layout)
        if (this.bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
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
        val devicesAdapter: DevicesAdapter = DevicesAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = devicesAdapter
        lifecycleScope.launch {
            viewModel.uiState.collect{ state ->
                state.discoveredDevices.forEach {
                    Log.d("Collector", "$it")
                    devicesAdapter.addDevice(it)
                }
            }
        }
        return
    }

    fun onStartDiscovery(v: View) {
        this.viewModel.startDiscovery()
        return
    }

    fun onStopDiscovery(v: View) {
        this.viewModel.uiState.value.discoveredDevices.forEach {
            Log.d("Main", "Found: ${it.name}")
        }
        this.viewModel.cancelDiscovery()
    }

}

@Composable
fun Greeting(name: String, ryba: String, modifier: Modifier = Modifier) {
    Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(8.dp)) {
        Text(
            text = "Hello $name!",
            fontSize = 10.sp,
            modifier = modifier
        )
        Text(text = "Ja Kruta $ryba")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Ryba", "Sasiska")
    }
}