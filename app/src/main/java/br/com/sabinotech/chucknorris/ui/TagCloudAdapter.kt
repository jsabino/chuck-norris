package br.com.sabinotech.chucknorris.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Category
import kotlinx.android.synthetic.main.category.view.*

class TagCloudAdapter(private val categories: List<Category>, private val clickListener: ClickListener) :
    RecyclerView.Adapter<TagCloudViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagCloudViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category, parent, false)
        return TagCloudViewHolder(view)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: TagCloudViewHolder, position: Int) {
        val category = categories[position]
        holder.setCategory(category)
        holder.setClickListener {
            clickListener.onClick(category)
        }
    }

    interface ClickListener {
        fun onClick(category: Category)
    }
}

class TagCloudViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setCategory(category: Category) {
        itemView.categoryName.text = category.name
    }

    fun setClickListener(clickListener: () -> Unit) {
        itemView.setOnClickListener {
            clickListener()
        }
    }
}