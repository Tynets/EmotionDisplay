package gp.gridpaint.CategoryElementSelection

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gp.gridpaint.GridPainter.MainActivity
import gp.gridpaint.R
import java.io.ByteArrayOutputStream


class CategoryElementSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_element_selection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val colors = IntArray(256){Color.WHITE}
        for (i in 100..150) colors[i] = Color.BLUE
        colors[0] = Color.RED
        colors[255] = Color.RED
        val bitmap = Bitmap.createBitmap(colors, 16, 16, Bitmap.Config.RGB_565)

        val category = intent.getIntExtra("Category", 0)
        val list = ArrayList<CategoryElementModel>()
        for (i in 1..10) {
            list.add(CategoryElementModel(if (category == 0) bitmap else bitmap, if (category == 0) "Eyes $i" else "Mouth $i"))
        }
        val adapter = CategoryElementViewAdapter(list) { data ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ID", data.title)
            // bitmap
            val stream = ByteArrayOutputStream()
            data.image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            intent.putExtra("Bitmap", byteArray)
            //
            startActivity(intent)
        }
        val recView: RecyclerView = findViewById(R.id.recycler_view_cat_el)
        recView.layoutManager = GridLayoutManager(this, 2)
        recView.adapter = adapter
    }
}