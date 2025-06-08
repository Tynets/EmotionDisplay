package gp.gridpaint.CategoryElementSelection

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gp.gridpaint.GridPainter.MainActivity
import gp.gridpaint.R

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
        val category = intent.getStringExtra("Category")

        val list = ArrayList<CategoryElementModel>()
        for (i in 1..10) {
            val bitmap = BitmapFactory.decodeFile(this.filesDir.toString() + "/$category/$i.png")
            list.add(CategoryElementModel(bitmap, "$i"))
        }

        val adapter = CategoryElementViewAdapter(list) { data ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Element", category + "/" + data.title)
            startActivity(intent)
        }
        val recView: RecyclerView = findViewById(R.id.recycler_view_cat_el)
        recView.layoutManager = GridLayoutManager(this, 2)
        recView.adapter = adapter
    }
}