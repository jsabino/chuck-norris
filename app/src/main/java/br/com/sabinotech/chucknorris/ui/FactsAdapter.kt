package br.com.sabinotech.chucknorris.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Fact
import kotlinx.android.synthetic.main.chuck_norris_fact.view.*

class FactsAdapter(private val facts: List<Fact>) : RecyclerView.Adapter<FactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chuck_norris_fact, parent, false)

        return FactViewHolder(view)
    }

    override fun getItemCount(): Int = facts.size

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        holder.setFact(facts[position])
    }
}

class FactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setFact(fact: Fact) {
        itemView.factText.text = fact.text
        itemView.factCategory.text = getFactCategoryName(fact)
    }

    private fun getFactCategoryName(fact: Fact) = fact.category ?: "UNCATEGORIZED"
}

