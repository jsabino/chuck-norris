package br.com.sabinotech.chucknorris.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.sabinotech.chucknorris.common.testObserver
import br.com.sabinotech.chucknorris.data.repositories.FactsRepository
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val factsRepository by lazy { mock(FactsRepository::class.java) }
    private val networkStateOnline by lazy { Observable.just(true) }
    private val networkStateOffline by lazy { Observable.just(false) }
    private val mainViewModel by lazy { MainViewModel(factsRepository, networkStateOnline, Schedulers.trampoline()) }

    @Test
    fun `getFacts first state must be a empty`() {
        val testObserver = mainViewModel.isLoading().testObserver()
        Assert.assertEquals(listOf<Fact>(), testObserver.observedValues)
    }

    @Test
    fun `isLoading first state must be empty`() {
        val testObserver = mainViewModel.isLoading().testObserver()
        Assert.assertEquals(listOf<Boolean>(), testObserver.observedValues)
    }

    @Test
    fun `when internet connection is available isInternetAvailable must be true`() {
        val testObserver = mainViewModel.isInternetAvailable().testObserver()
        Assert.assertEquals(listOf(true), testObserver.observedValues)
    }

    @Test
    fun `when internet connection is unavailable isInternetAvailable must be false`() {
        val mainViewModel = MainViewModel(factsRepository, networkStateOffline, Schedulers.trampoline())

        val testObserver = mainViewModel.isInternetAvailable().testObserver()
        Assert.assertEquals(listOf(false), testObserver.observedValues)
    }

    @Test
    fun `when searchTerm is set the facts must be updated`() {
        val expectedFacts = listOf(
            Fact("a2", "http://google.com", "Dev Joke 1", "DEV")
        )

        `when`(factsRepository.queryFacts("DEV")).thenReturn(Single.just(expectedFacts))

        val testObserver = mainViewModel.getFacts().testObserver()

        mainViewModel.setSearchTerm("DEV")
        Assert.assertEquals(listOf(listOf(), expectedFacts), testObserver.observedValues)
    }

    @Test
    fun `when searchTerm is set the loading must be triggered`() {
        `when`(factsRepository.queryFacts("DEV")).thenReturn(Single.just(listOf()))

        val testObserver = mainViewModel.isLoading().testObserver()

        mainViewModel.setSearchTerm("DEV")
        Assert.assertEquals(listOf(true, false), testObserver.observedValues)
    }
}
