package br.com.sabinotech.chucknorris.ui.viewmodels

import androidx.lifecycle.ViewModel
import br.com.sabinotech.chucknorris.data.repositories.FactsRepository

class SearchViewModel(private val repository: FactsRepository) : ViewModel() {

    fun changeSearchTerm(newSearchTerm:String) {
        repository.changeSearchTerm(newSearchTerm)
    }
}
