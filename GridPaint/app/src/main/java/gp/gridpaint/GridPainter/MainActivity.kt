package gp.gridpaint.GridPainter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.get
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import gp.gridpaint.CategoryElementSelection.CategoryElementSelectionActivity
import gp.gridpaint.R
import java.io.FileOutputStream

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
            var pos: Int = i
            if (y % 2 == 0) pos = i + (15 - 2 * x)
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
            for (i in 0..255) colors[i] = list[i].color
            val bitmapUpdate = Bitmap.createBitmap(colors, 16, 16, Bitmap.Config.RGB_565)
            val stream = FileOutputStream(this.filesDir.toString() + "/$element.png")
            bitmapUpdate.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            val intent = Intent(this, CategoryElementSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}