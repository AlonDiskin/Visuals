package com.diskin.alon.visuals.recuclebin.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class TrashedItemsViewModelImpl(
    repository: TrashedItemRepository
) : ViewModel(), TrashedItemsViewModel {

    private val _trashedItems = MutableLiveData<List<TrashedItem>>()
    override val trashedItems: LiveData<List<TrashedItem>>
        get() = _trashedItems

    private val disposable = CompositeDisposable()

    init {
        val subscription = repository.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { EspressoIdlingResource.increment() }
            .doOnNext { EspressoIdlingResource.decrement() }
            .doFinally { EspressoIdlingResource.decrement() }
            .subscribe({
                _trashedItems.value = it
            },Throwable::printStackTrace)

        disposable.add(subscription)
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}