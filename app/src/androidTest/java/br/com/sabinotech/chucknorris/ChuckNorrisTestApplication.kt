package br.com.sabinotech.chucknorris

import android.app.Application
import androidx.room.Room
import br.com.sabinotech.chucknorris.data.local.AppDatabase
import br.com.sabinotech.chucknorris.data.repositories.FactsRepository
import br.com.sabinotech.chucknorris.data.repositories.FactsRepositoryInterface
import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.ui.common.NetworkState
import br.com.sabinotech.chucknorris.ui.viewmodels.ViewModelFactory
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.mockito.Mockito

class ChuckNorrisTestApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {

        import(androidXModule(this@ChuckNorrisTestApplication))

        bind<DKodein>() with singleton { kodein.direct }

        bind<FactsRepositoryInterface>() with singleton { FactsRepository(instance(), instance()) }

        bind<ViewModelFactory>() with singleton { ViewModelFactory(instance()) }

        bind<AppDatabase>() with singleton {
            Room
                .inMemoryDatabaseBuilder(
                    applicationContext,
                    AppDatabase::class.java
                )
                .build()
        }

        bind<ChuckNorrisService>() with singleton {
            Mockito.mock(ChuckNorrisService::class.java)
        }

        bind<NetworkState>() with singleton { NetworkState() }

    }
}