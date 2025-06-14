package gp.gridpaint.categorySelection

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import gp.gridpaint.categoryElementSelection.CategoryElementSelectionActivity
import gp.gridpaint.R

class CategorySelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_selection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*
        val colors = IntArray(256) { Color.BLACK }
        for (i in 1..10) {
            val bitmap = Bitmap.createBitmap(colors, 16, 16, Bitmap.Config.RGB_565)
            val stream = FileOutputStream(this.filesDir.toString() + "/Eyes/$i.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            val stream2 = FileOutputStream(this.filesDir.toString() + "/Mouths/$i.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream2)
            stream2.flush()
            stream2.close()
        }
         */

        val eyes: TextView = findViewById(R.id.text_view_eyes)
        eyes.setOnClickListener {
            val intent = Intent(this, CategoryElementSelectionActivity::class.java)
            intent.putExtra("Category", "Eyes")
            startActivity(intent)
        }

        val mouths: TextView = findViewById(R.id.text_view_mouths)
        mouths.setOnClickListener {
            val intent = Intent(this, CategoryElementSelectionActivity::class.java)
            intent.putExtra("Category", "Mouths")
            startActivity(intent)
        }
    }
}