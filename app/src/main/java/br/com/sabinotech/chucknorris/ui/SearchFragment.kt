package br.com.sabinotech.chucknorris.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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

        initCategoriesObserver()

        initPastSearchesObserver()
    }

    private fun setSearchTermListener() {
        searchTerm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                onChangeSearchTermListener.onChangeSearchTerm(searchTerm.text?.toString() ?: "")
                true
            } else {
                false
            }
        }
    }

    private fun initCategoriesObserver() {
        viewModel.getCategories().observe(this, Observer { categories ->
            val selectedCategories = categories.shuffled().take(NUMBER_OF_DISPLAYED_TAGS)

            val adapter = TagCloudAdapter(selectedCategories, getTagClickListener())
            searchTagCloud.layoutManager = FlexboxLayoutManager(activity)
            searchTagCloud.adapter = adapter
        })
    }

    private fun initPastSearchesObserver() {
        viewModel.getPastSearches().observe(this, Observer { list ->
            context?.run {
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
                pastSearches.adapter = adapter
                pastSearches.setOnItemClickListener { _, _, position, _ ->
                    onChangeSearchTermListener.onChangeSearchTerm(list[position])
                }
            }
        })
    }

    private fun getTagClickListener(): TagCloudAdapter.ClickListener {
        return object : TagCloudAdapter.ClickListener {
            override fun onClick(category: Category) {
                onChangeSearchTermListener.onChangeSearchTerm(category.name)
            }
        }
    }

    private fun hideKeyboard() {
        context?.run {
            val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE)
            if (imm is InputMethodManager) {
                imm.hideSoftInputFromWindow(searchTerm.windowToken, 0)
            }
        }

        searchTerm.clearFocus()
    }

    interface OnChangeSearchTermListener {

        fun onChangeSearchTerm(newSearchTerm: String)
    }
}
