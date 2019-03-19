package br.com.sabinotech.chucknorris.ui.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import br.com.sabinotech.chucknorris.ui.viewmodels.MainViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

abstract class BaseFragment : Fragment(), KodeinAware {

    override val kodein: Kodein by kodein()
    private val viewModeFactory: ViewModelProvider.Factory by instance()
    protected val viewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this, viewModeFactory).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid activity")
    }
}