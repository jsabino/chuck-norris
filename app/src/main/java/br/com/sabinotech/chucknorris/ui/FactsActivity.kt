package br.com.sabinotech.chucknorris.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_facts.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FactsActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: FactsViewModel by instance()
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facts)
    }

    override fun onResume() {
        super.onResume()
        queryFacts()
    }

    private fun queryFacts() {
        val disposable = viewModel.queryFacts()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgressBar() }
            .doAfterTerminate { hideProgressBar() }
            .subscribe(Consumer {
                updateRecyclerView(it)
            })
        disposables.add(disposable)
    }

    private fun updateRecyclerView(facts: List<Fact>) {
        val adapter = FactsAdapter(facts)
        mainRecyclerView.layoutManager = LinearLayoutManager(this)
        mainRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_search) {
            openSearchActivity()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    private fun showProgressBar() {
        factsProgressBar.visibility = ProgressBar.VISIBLE
    }

    private fun hideProgressBar() {
        factsProgressBar.visibility = ProgressBar.GONE
    }
}
