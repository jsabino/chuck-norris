package br.com.sabinotech.chucknorris.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.sabinotech.chucknorris.common.testObserver
import br.com.sabinotech.chucknorris.data.repositories.FactsRepositoryInterface
import br.com.sabinotech.chucknorris.domain.Category
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val factsRepository by lazy { mock(FactsRepositoryInterface::class.java) }
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
        `when`(factsRepository.saveSearch("DEV")).thenReturn(Completable.complete())

        val testObserver = mainViewModel.getFacts().testObserver()

        mainViewModel.setSearchTerm("DEV")
        Assert.assertEquals(listOf(listOf(), expectedFacts), testObserver.observedValues)
    }

    @Test
    fun `when searchTerm is set the loading must be triggered`() {
        `when`(factsRepository.queryFacts("DEV")).thenReturn(Single.just(listOf()))
        `when`(factsRepository.saveSearch("DEV")).thenReturn(Completable.complete())

        val testObserver = mainViewModel.isLoading().testObserver()

        mainViewModel.setSearchTerm("DEV")
        Assert.assertEquals(listOf(true, false), testObserver.observedValues)
    }

    @Test
    fun `when searchTerm is set the term must be saved on database`() {
        `when`(factsRepository.queryFacts("DEV")).thenReturn(Single.just(listOf()))

        var isSearchSaved = false
        `when`(factsRepository.saveSearch("DEV")).thenAnswer {
            isSearchSaved = true
            Completable.complete()
        }

        mainViewModel.setSearchTerm("DEV")
        Assert.assertEquals(true, isSearchSaved)
    }

    @Test
    fun `getCategories should return an empty list if the repository has no category`() {
        val expectedCategories = listOf<Category>()

        `when`(factsRepository.getCategories()).thenReturn(Maybe.just(expectedCategories))

        val testObserver = mainViewModel.getCategories().testObserver()
        Assert.assertEquals(listOf(expectedCategories), testObserver.observedValues)
    }

    @Test
    fun `getCategories should return the list of categories that the repository returns`() {
        val expectedCategories = listOf(Category("DEV"))

        `when`(factsRepository.getCategories()).thenReturn(Maybe.just(expectedCategories))

        val testObserver = mainViewModel.getCategories().testObserver()
        Assert.assertEquals(listOf(expectedCategories), testObserver.observedValues)
    }

    @Test
    fun `get notified about errors getting facts`() {
        `when`(factsRepository.saveSearch("DEV")).thenReturn(Completable.complete())
        `when`(factsRepository.queryFacts("DEV")).thenReturn(Single.error(RuntimeException("Test")))

        val testObserver = mainViewModel.getErrors().testObserver()
        mainViewModel.setSearchTerm("DEV")

        Assert.assertEquals(listOf("There was an error loading the facts"), testObserver.observedValues)
    }

    @Test
    fun `get notified about errors saving search term`() {
        `when`(factsRepository.saveSearch("DEV")).thenReturn(Completable.error(RuntimeException("Test")))
        `when`(factsRepository.queryFacts("DEV")).thenReturn(Single.just(listOf()))

        val testObserver = mainViewModel.getErrors().testObserver()
        mainViewModel.setSearchTerm("DEV")

        Assert.assertEquals(listOf("There was an error saving your search"), testObserver.observedValues)
    }

    @Test
    fun `get notified about errors getting categories`() {
        `when`(factsRepository.getCategories()).thenReturn(Maybe.error(RuntimeException("Test")))

        val testObserver = mainViewModel.getErrors().testObserver()
        mainViewModel.getCategories()

        Assert.assertEquals(listOf("There was an error loading the suggestions"), testObserver.observedValues)
    }

    @Test
    fun `get notified about errors getting past searches`() {
        `when`(factsRepository.getPastSearches(Mockito.anyInt())).thenReturn(Observable.error(RuntimeException("Test")))

        val testObserver = mainViewModel.getErrors().testObserver()
        mainViewModel.getPastSearches()

        Assert.assertEquals(listOf("There was an error loading the past searches"), testObserver.observedValues)
    }
}
