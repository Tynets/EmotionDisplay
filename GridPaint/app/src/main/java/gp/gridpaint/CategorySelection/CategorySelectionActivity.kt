package gp.gridpaint.CategorySelection

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import gp.gridpaint.CategoryElementSelection.CategoryElementSelectionActivity
import gp.gridpaint.GridPainter.MainActivity
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

        val eyes: TextView = findViewById(R.id.text_view_eyes)
        eyes.setOnClickListener {
            val intent = Intent(this, CategoryElementSelectionActivity::class.java)
            intent.putExtra("Category", 0)
            startActivity(intent)
        }

        val mouths: TextView = findViewById(R.id.text_view_mouths)
        mouths.setOnClickListener {
            val intent = Intent(this, CategoryElementSelectionActivity::class.java)
            intent.putExtra("Category", 1)
            startActivity(intent)
        }
    }
}