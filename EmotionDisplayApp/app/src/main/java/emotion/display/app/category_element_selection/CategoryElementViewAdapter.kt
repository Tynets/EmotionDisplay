package emotion.display.app.category_element_selection

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emotion.display.app.R

class CategoryElementViewAdapter(private val list: List<CategoryElementModel>, val listener: (CategoryElementModel) -> Unit): RecyclerView.Adapter<CategoryElementHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryElementHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_element, parent, false)
        return CategoryElementHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryElementHolder, position: Int) {
        val item = list[position]
        holder.textView.text = item.title
        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(item.image, 256 * 2, 256 * 2, false))
        holder.itemView.setOnClickListener {
            listener(item)
        }
    }
}

class CategoryElementHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.image_view_cat_el)
    val textView: TextView = itemView.findViewById(R.id.text_view_cat_el)
}