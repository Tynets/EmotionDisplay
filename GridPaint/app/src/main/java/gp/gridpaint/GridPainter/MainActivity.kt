package gp.gridpaint.GridPainter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import gp.gridpaint.R

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
        var color: Int = Color.WHITE
        val element: String? = intent.getStringExtra("ID")

        val gridView: GridView = findViewById(R.id.grid)
        val list = ArrayList<PixelModel>()
        val byteArray = intent.getByteArrayExtra("Bitmap")
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size);
        for(i in 0..255) list.add(PixelModel(bitmap.getPixel(i % 16, i / 16), i))
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
            for (l in list) l.color = Color.WHITE
            gridView.invalidateViews()
        }

        val fillButton: Button = findViewById(R.id.fill_button)
        fillButton.setOnClickListener {
            for (l in list) l.color = color
            gridView.invalidateViews()
        }
    }
}