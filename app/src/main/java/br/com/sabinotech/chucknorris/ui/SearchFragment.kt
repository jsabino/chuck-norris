package br.com.sabinotech.chucknorris.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Category
import br.com.sabinotech.chucknorris.ui.adapters.TagCloudAdapter
import br.com.sabinotech.chucknorris.ui.common.BaseFragment
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_search.*

const val NUMBER_OF_DISPLAYED_TAGS = 8

class SearchFragment : BaseFragment() {

    private lateinit var onChangeSearchTermListener: OnChangeSearchTermListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnChangeSearchTermListener) {
            onChangeSearchTermListener = context
            return
        }

        throw ClassCastException("$context must implement OnChangeSearchTermListener")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onResume() {
        super.onResume()

        setSearchTermListener()

        viewModel.getCategories().observe(this, Observer { categories ->
            updateTagsCloud(categories.shuffled().take(NUMBER_OF_DISPLAYED_TAGS))
        })
    }

    private fun setSearchTermListener() {
        searchTerm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onChangeSearchTermListener.onChangeSearchTerm(searchTerm.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun updateTagsCloud(categories: List<Category>) {
        val adapter = TagCloudAdapter(categories, getTagClickListener())
        searchTagCloud.layoutManager = FlexboxLayoutManager(activity)
        searchTagCloud.adapter = adapter
    }

    private fun getTagClickListener(): TagCloudAdapter.ClickListener {
        return object : TagCloudAdapter.ClickListener {
            override fun onClick(category: Category) {
                onChangeSearchTermListener.onChangeSearchTerm(category.name)
            }
        }
    }

    interface OnChangeSearchTermListener {

        fun onChangeSearchTerm(newSearchTerm: String)
    }
}
