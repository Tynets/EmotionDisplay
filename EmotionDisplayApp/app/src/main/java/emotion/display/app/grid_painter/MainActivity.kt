package emotion.display.app.grid_painter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import emotion.display.app.R
import emotion.display.app.bluetooth_device_selection.bluetooth.BluetoothController
import emotion.display.app.bluetooth_device_selection.data.toMessage
import emotion.display.app.category_element_selection.CategoryElementSelectionActivity
import java.io.FileOutputStream

fun Int.toSignedByte() : Byte {
    return if (this > 255) throw IllegalArgumentException("Must be lower that 256")
    else if (this < 128) this.toByte()
    else return (-(this.inv() + 1)).toByte()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var color: Int = Color.BLUE
        val element: String? = intent.getStringExtra("Element")

        val gridView: GridView = findViewById(R.id.grid)
        val list = ArrayList<PixelModel>()
        val bitmap = BitmapFactory.decodeFile(this.filesDir.toString() + "/$element.png")
        for (i in 0..255) {
            val x: Int = i % 16
            val y: Int = i / 16
            var pos: Int = 255 - i
            if (y % 2 == 1) pos = 255 - i - (15 - 2 * x)
            list.add(PixelModel(bitmap[x, y], pos))
        }
        val adapter = GridViewAdapter(this, list)
        gridView.adapter = adapter
        gridView.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            list[position].color = color
            gridView.invalidateViews()
        }

        val gridViewColors: GridView = findViewById(R.id.grid_colors)
        val listColors = ArrayList<PixelModel>()
        listColors.add(PixelModel(Color.BLUE, 1))
        listColors.add(PixelModel(Color.RED, 1))
        listColors.add(PixelModel(Color.YELLOW, 1))
        listColors.add(PixelModel(Color.GREEN, 1))
        listColors.add(PixelModel(Color.BLACK, 1))
        listColors.add(PixelModel(Color.MAGENTA, 1))
        listColors.add(PixelModel(Color.CYAN, 1))
        listColors.add(PixelModel(Color.GRAY, 1))
        listColors.add(PixelModel(Color.WHITE, 1))
        val adapterColors = GridViewAdapter(this, listColors)
        gridViewColors.adapter = adapterColors
        gridViewColors.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            color = listColors[position].color
        }

        val clearButton: Button = findViewById(R.id.clear_button)
        clearButton.setOnClickListener {
            for (l in list) l.color = Color.BLACK
            gridView.invalidateViews()
        }

        val fillButton: Button = findViewById(R.id.fill_button)
        fillButton.setOnClickListener {
            for (l in list) l.color = color
            gridView.invalidateViews()
        }

        val updateButton: Button = findViewById(R.id.update_button)
        updateButton.setOnClickListener {
            val colors = IntArray(256)
            val pixelsToSend = arrayListOf<PixelModel>()
            for (i in 0..255) {
                colors[i] = list[i].color
                if (list[i].color != Color.BLACK) pixelsToSend.add(list[i])
            }
            val bitmapUpdate = Bitmap.createBitmap(colors, 16, 16, Bitmap.Config.RGB_565)
            val stream = FileOutputStream(this.filesDir.toString() + "/$element.png")
            bitmapUpdate.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            val size: Int = pixelsToSend.size * 5
            val message = ByteArray(5 + size)
            val elementSplit  = element!!.split("/")
            message[0] = 'u'.code.toSignedByte()
            message[1] = when (elementSplit[0]){
                "Eyes" -> 1.toSignedByte()
                "Mouths" -> 0.toSignedByte()
                else -> return@setOnClickListener
            }
            message[2] = elementSplit[1].toInt().toSignedByte()
            message[3] = (size and 0xFF).toSignedByte()
            message[4] = (size shr 8).toSignedByte()
            for (i in 5..size step 5) {
                val pixel = pixelsToSend.removeAt(0)
                message[i] = (pixel.pos and 0xFF).toSignedByte()
                message[i + 1] = (pixel.pos shr 8).toSignedByte()
                message[i + 2] = pixel.color.red.toSignedByte()
                message[i + 3] = pixel.color.green.toSignedByte()
                message[i + 4] = pixel.color.blue.toSignedByte()
            }
            val bluetoothController = BluetoothController.getInstance(applicationContext)
            bluetoothController.sendMessage(message.toMessage())
            Toast.makeText(this, "${elementSplit[0]} updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CategoryElementSelectionActivity::class.java)
            intent.putExtra("Category", elementSplit[0])
            startActivity(intent)
        }
    }
}