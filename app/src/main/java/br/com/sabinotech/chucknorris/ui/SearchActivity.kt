package br.com.sabinotech.chucknorris.ui

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Category
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SearchActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: SearchViewModel by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val categories = listOf(
            Category("OBAMA"),
            Category("CAR"),
            Category("DEV")
        )

        setSearchTermListener()

        updateTagCloud(categories)
    }

    private fun setSearchTermListener() {
        searchTerm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeSearchTerm(searchTerm.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun updateTagCloud(categories:List<Category>) {
        val adapter = TagCloudAdapter(categories, getTagClickListener())
        searchTagCloud.layoutManager = FlexboxLayoutManager(this)
        searchTagCloud.adapter = adapter
    }

    private fun getTagClickListener(): TagCloudAdapter.ClickListener {
        return object : TagCloudAdapter.ClickListener {
            override fun onClick(category: Category) {
                changeSearchTerm(category.name)
            }
        }
    }

    fun changeSearchTerm(newSearchTerm:String) {
        viewModel.changeSearchTerm(newSearchTerm)
        finish()
    }
}
