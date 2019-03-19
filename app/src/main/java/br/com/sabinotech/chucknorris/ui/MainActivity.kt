package br.com.sabinotech.chucknorris.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.ui.viewmodels.MainViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity :
    AppCompatActivity(),
    KodeinAware,
    SearchFragment.OnChangeSearchTermListener,
    FactsFragment.OnRequestSearchListener {

    override val kodein by kodein()
    private val viewModeFactory: ViewModelProvider.Factory by instance()
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModeFactory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            showFacts()
        }
    }

    override fun onChangeSearchTerm(newSearchTerm: String) {
        showFacts()
        viewModel.setSearchTerm(newSearchTerm)
    }

    override fun onRequestSearch() {
        showSearch()
    }

    private fun showFacts() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, FactsFragment())
        transaction.commit()
    }

    private fun showSearch() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.addToBackStack("search")
        transaction.replace(R.id.frameLayout, SearchFragment())
        transaction.commit()
    }

}