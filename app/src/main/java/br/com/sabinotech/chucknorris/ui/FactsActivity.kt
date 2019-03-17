package br.com.sabinotech.chucknorris.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Fact
import br.com.sabinotech.chucknorris.ui.adapters.FactsAdapter
import br.com.sabinotech.chucknorris.ui.common.BaseActivity
import br.com.sabinotech.chucknorris.ui.viewmodels.FactsViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_facts.*
import org.kodein.di.generic.instance
import retrofit2.HttpException

class FactsActivity : BaseActivity() {

    private val viewModel: FactsViewModel by instance()
    private val snackbarRoot by lazy { findViewById<ViewGroup>(R.id.mainLayout) }
    private var persistentSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facts)

        initNetworkObserver()
    }

    private fun initNetworkObserver() {
        val disposable = networkState
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onNetworkStateChanged(it) }
        disposables.add(disposable)
    }

    private fun onNetworkStateChanged(isInternetAvailable: Boolean) {
        if (!isInternetAvailable) {
            persistentSnackbar = Snackbar.make(snackbarRoot, "No internet connection", Snackbar.LENGTH_INDEFINITE)
            persistentSnackbar?.show()
            return
        }

        persistentSnackbar?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        queryFacts()
    }

    private fun queryFacts() {
        if (!networkState.isInternetAvailable()) {
            return
        }

        val disposable = viewModel.queryFacts()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                updateRecyclerView(listOf())
                showProgressBar()
            }
            .doAfterTerminate { hideProgressBar() }
            .subscribe(Consumer {
                if (it.isSuccess) {
                    updateRecyclerView(it.getOrThrow())
                } else {
                    val errorMsg = it.exceptionOrNull()?.let { t ->
                        if (t is HttpException) {
                            t.response().body().toString()
                        } else {
                            t::class.toString() + " " + t.message
                        }
                    } ?: "unknown error"

                    Snackbar.make(snackbarRoot, errorMsg, Snackbar.LENGTH_LONG).show()
                }
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

    private fun showProgressBar() {
        factsProgressBar.visibility = ProgressBar.VISIBLE
    }

    private fun hideProgressBar() {
        factsProgressBar.visibility = ProgressBar.GONE
    }
}
