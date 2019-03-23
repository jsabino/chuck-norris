package br.com.sabinotech.chucknorris.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Category
import kotlinx.android.synthetic.main.category.view.*

class TagCloudAdapter(
    private val clickListener: ClickListener
) : RecyclerView.Adapter<TagCloudViewHolder>() {

    private val categories = mutableListOf<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagCloudViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category, parent, false)
        return TagCloudViewHolder(view)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: TagCloudViewHolder, position: Int) {
        categories[position].run {
            holder.setCategory(this)
            holder.setClickListener {
                clickListener.onClick(this)
            }
        }
    }

    fun changeItems(newCategories: List<Category>) {
        categories.clear()
        categories.addAll(newCategories)
        notifyDataSetChanged()
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