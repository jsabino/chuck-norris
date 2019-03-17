package br.com.sabinotech.chucknorris.ui.viewmodels

import androidx.lifecycle.ViewModel
import br.com.sabinotech.chucknorris.data.repositories.FactsRepository
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single

class FactsViewModel(private val repository: FactsRepository) : ViewModel() {

    fun queryFacts(): Single<Result<List<Fact>>> {
        return repository
            .queryFacts()
            .map { Result.success(it) }
            .onErrorResumeNext { Single.just(Result.failure(it)) }
    }
}
