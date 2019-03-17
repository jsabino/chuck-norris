package br.com.sabinotech.chucknorris.ui

import androidx.lifecycle.ViewModel
import br.com.sabinotech.chucknorris.data.repositories.FactsRepository
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single

class FactsViewModel(private val repository: FactsRepository) : ViewModel() {

    fun queryFacts(): Single<List<Fact>> {
        return repository.queryFacts()
    }
}
