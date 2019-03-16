package br.com.sabinotech.chucknorris

import android.app.Application
import br.com.sabinotech.chucknorris.data.repositories.FactsRepository
import br.com.sabinotech.chucknorris.data.repositories.FactsTestRepository
import br.com.sabinotech.chucknorris.ui.FactsViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ChuckNorrisApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        bind<FactsRepository>() with singleton { FactsTestRepository() }
        bind<FactsViewModel>() with provider { FactsViewModel(instance()) }
    }
}
