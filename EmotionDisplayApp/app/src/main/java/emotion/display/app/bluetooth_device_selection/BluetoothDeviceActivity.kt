package emotion.display.app.bluetooth_device_selection

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import emotion.display.app.R
import emotion.display.app.bluetooth_device_selection.adapter.DevicesAdapter
import emotion.display.app.bluetooth_device_selection.bluetooth.BluetoothViewModel
import emotion.display.app.bluetooth_device_selection.data.Message
import emotion.display.app.bluetooth_device_selection.data.toStr
import emotion.display.app.bluetooth_device_selection.data.toUnsignedInt
import emotion.display.app.category_selection.CategorySelectionActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class BluetoothDeviceActivity : AppCompatActivity() {

    private val bluetoothManager: BluetoothManager? by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager?.adapter
    }

    private val viewModel: BluetoothViewModel by viewModels()

    private lateinit var progressBar: ProgressBar

    private val tag: String = "BluetoothDeviceActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bluetooth_device_selection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val connectionStatus: TextView = this.findViewById(R.id.connectionStatus)
        this.progressBar = this.findViewById(R.id.progressBar)

        if (this.bluetoothAdapter == null) {
            Toast.makeText(this, "No Bluetooth support", Toast.LENGTH_SHORT).show()
            return
        }
        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
            )
        }
        try {
            if (this.bluetoothAdapter?.isEnabled == false) {
                val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if (result.resultCode != Activity.RESULT_OK) {
                        return@registerForActivityResult
                    }
                }
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startForResult.launch(enableBtIntent)
            }
        } catch (e: SecurityException) {
            Log.e(tag, e.toString())
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            return
        }

        val pairedAdapter = DevicesAdapter { device ->
            this.viewModel.connectToDevice(device)
        }
        val pairedRecyclerView: RecyclerView = findViewById(R.id.pairedRecyclerView)
        pairedRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedRecyclerView.adapter = pairedAdapter

        val discoveredAdapter = DevicesAdapter { device ->
            this.viewModel.connectToDevice(device)
        }

        val discoveredRecyclerView: RecyclerView = findViewById(R.id.discoveredRecyclerView)
        discoveredRecyclerView.layoutManager = LinearLayoutManager(this)
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
            viewModel.messages.collect { Toast.makeText(this@BluetoothDeviceActivity, it, Toast.LENGTH_SHORT).show()
                if (it == "Connected") {
                    viewModel.startListening()
                    setContentView(R.layout.load_layout)
                    val message = Message('a'.code.toByte())
                    viewModel.sendMessage(message)
                    Log.d(tag, "Sent ${message.toStr()}")
                }
            }
        }
        val eyesPath = filesDir.toString() + "/" + getString(R.string.eyesFolder)
        val mouthsPath = filesDir.toString() + "/" + getString(R.string.mouthsFolder)
        File(eyesPath).mkdirs()
        File(mouthsPath).mkdirs()
        var messageCount = 0
        lifecycleScope.launch {
            viewModel.bluetoothMessages.collect {
                Log.d(tag, "Received ${it.toStr()}")
                if (it.command == 'a'.code.toByte()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        it.pixels?.let { pixels->
                            val path = if (it.category == 0.toByte()) mouthsPath else eyesPath
                            val colors = IntArray(256) { Color.BLACK }
                            for (i in pixels.indices step 5) {
                                val position: Int = pixels[i].toUnsignedInt()
                                val y = 15 - position / 16
                                val x = if (y % 2 == 1) position % 16 else 15 - position % 16
                                colors[y * 16 + x] = Color.rgb(pixels[i + 2].toUnsignedInt(),
                                    pixels[i + 3].toUnsignedInt(), pixels[i + 4].toUnsignedInt())
                            }
                            val bitmap = Bitmap.createBitmap(colors, 16, 16, Bitmap.Config.RGB_565)
                            FileOutputStream("$path/${it.position}.png").use { fos ->
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                                fos.flush()
                            }
                        }
                        messageCount++
                        if (messageCount == 20) {
                            val intent = Intent(this@BluetoothDeviceActivity, CategorySelectionActivity::class.java)
                            startActivity(intent)
                        }
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

    fun onStopDiscovery(v: View) {
        this.viewModel.cancelDiscovery()
    }

}