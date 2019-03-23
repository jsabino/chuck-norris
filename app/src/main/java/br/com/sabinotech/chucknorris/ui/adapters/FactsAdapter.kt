package br.com.sabinotech.chucknorris.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Fact
import kotlinx.android.synthetic.main.chuck_norris_fact.view.*

class FactsAdapter(
    private val shareButtonClickListener: ShareButtonClickListener
) : RecyclerView.Adapter<FactViewHolder>() {

    private val facts = mutableListOf<Fact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chuck_norris_fact, parent, false)

        return FactViewHolder(view)
    }

    override fun getItemCount(): Int = facts.size

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        facts[position].run {
            holder.setFact(this)
            holder.setShareButtonClickListener {
                shareButtonClickListener.onShareButtonClicked(this)
            }
        }
    }

    fun changeItems(newFacts: List<Fact>) {
        facts.clear()
        facts.addAll(newFacts)
        notifyDataSetChanged()
    }

    interface ShareButtonClickListener {
        fun onShareButtonClicked(fact: Fact)
    }
}

class FactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setFact(fact: Fact) {
        itemView.factText.text = fact.text
        itemView.factText.textSize = fact.getTextSize()
        itemView.factCategory.text = fact.getCategoryName()
    }

    fun setShareButtonClickListener(shareButtonClickListener: () -> Unit) {
        itemView.factShareButton.setOnClickListener {
            shareButtonClickListener()
        }
    }
}
