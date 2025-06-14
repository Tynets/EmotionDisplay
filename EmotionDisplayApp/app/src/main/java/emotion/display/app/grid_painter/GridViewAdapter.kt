package emotion.display.app.grid_painter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import emotion.display.app.R

class GridViewAdapter(context: Context, list: ArrayList<PixelModel>) : ArrayAdapter<PixelModel>(context, 0, list as List<PixelModel>) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)
        }
        val model: PixelModel? = getItem(position)
        val imageView = itemView!!.findViewById<ImageView>(R.id.image_view_grid)
        imageView.setColorFilter(model!!.color)
        return itemView
    }
}