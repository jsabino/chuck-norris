package br.com.sabinotech.chucknorris.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.domain.Fact
import br.com.sabinotech.chucknorris.ui.adapters.FactsAdapter
import br.com.sabinotech.chucknorris.ui.common.BaseFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_facts.*
import kotlinx.android.synthetic.main.fragment_facts.view.*

class FactsFragment : BaseFragment() {

    private lateinit var snackbarRoot: ViewGroup
    private var persistentSnackbar: Snackbar? = null
    private lateinit var onRequestSearchListener: OnRequestSearchListener
    private val factsAdapter = FactsAdapter(getShareButtonClickListener())

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnRequestSearchListener) {
            onRequestSearchListener = context
            return
        }

        throw ClassCastException("$context must implement OnRequestSearchListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_facts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        snackbarRoot = view!!.fragmentFactsMainLayout

        setupFactsAdapter()

        initInternetAvailabilityObserver()

        initLoadingObserver()

        initFactsObserver()

        initErrorObserver(snackbarRoot)
    }

    override fun onPause() {
        super.onPause()

        persistentSnackbar?.dismiss()
    }

    private fun setupFactsAdapter() {
        mainRecyclerView.layoutManager = LinearLayoutManager(activity)
        mainRecyclerView.adapter = factsAdapter
    }

    private fun initFactsObserver() {
        viewModel.getFacts().observe(viewLifecycleOwner, Observer {
            factsAdapter.changeItems(it)
        })
    }

    private fun initInternetAvailabilityObserver() {
        viewModel.isInternetAvailable().observe(viewLifecycleOwner, Observer { isAvailable ->
            if (isAvailable) {
                persistentSnackbar?.dismiss()
            } else {
                persistentSnackbar = Snackbar.make(snackbarRoot, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                persistentSnackbar?.show()
            }
        })
    }

    private fun initLoadingObserver() {
        viewModel.isLoading().observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                factsProgressBar.visibility = ProgressBar.VISIBLE
            } else {
                factsProgressBar.visibility = ProgressBar.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            onRequestSearchListener.onRequestSearch()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getShareButtonClickListener(): FactsAdapter.ShareButtonClickListener {
        return object : FactsAdapter.ShareButtonClickListener {
            override fun onShareButtonClicked(fact: Fact) {
                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Chuck Norris Fact")
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, fact.text)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))
            }
        }
    }

    interface OnRequestSearchListener {

        fun onRequestSearch()
    }
}
