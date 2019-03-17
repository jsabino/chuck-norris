package br.com.sabinotech.chucknorris.ui.common

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

abstract class BaseActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    protected val networkState: NetworkState by instance()
    protected val disposables = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()

        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}
