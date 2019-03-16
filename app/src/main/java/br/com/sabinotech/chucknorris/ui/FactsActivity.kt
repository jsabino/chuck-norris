package br.com.sabinotech.chucknorris.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FactsActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: FactsViewModel by instance()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        queryFacts()
    }

    private fun queryFacts() {
        val subscribe = viewModel.queryFacts().subscribe {
            updateRecyclerView(it)
        }
        disposable.add(subscribe)
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
        TODO("not implemented")
    }

    override fun onStop() {
        super.onStop()

        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
