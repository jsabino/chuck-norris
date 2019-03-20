package br.com.sabinotech.chucknorris

import android.app.Application
import androidx.room.Room
import br.com.sabinotech.chucknorris.data.local.AppDatabase
import br.com.sabinotech.chucknorris.data.repositories.FactsRepository
import br.com.sabinotech.chucknorris.data.repositories.FactsRepositoryInterface
import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.ui.common.NetworkState
import br.com.sabinotech.chucknorris.ui.viewmodels.ViewModelFactory
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChuckNorrisApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {

        import(androidXModule(this@ChuckNorrisApplication))

        bind<DKodein>() with singleton { kodein.direct }

        bind<FactsRepositoryInterface>() with singleton { FactsRepository(instance(), instance()) }

        bind<ViewModelFactory>() with singleton { ViewModelFactory(instance()) }

        bind<AppDatabase>() with singleton {
            Room
                .databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase.db"
                )
                .fallbackToDestructiveMigration()
                .build()
        }

        bind<ChuckNorrisService>() with singleton {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = okhttp3.OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            val build = Retrofit.Builder()
                .baseUrl("https://api.chucknorris.io/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            build.create(ChuckNorrisService::class.java)
        }

        bind<NetworkState>() with singleton { NetworkState() }
    }
}
