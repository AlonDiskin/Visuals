package com.diskin.alon.visuals.recuclebin.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class TrashedItemsViewModelImpl(
    repository: TrashedItemRepository,
    private val savedState: SavedStateHandle
) : ViewModel(), TrashedItemsViewModel {

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        val DEFAULT_FILTER = TrashedFilter.ALL
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        const val KEY_FILTER = "filter"
    }

    private val _trashedItems = MutableLiveData<List<TrashedItem>>()
    override val trashedItems: LiveData<List<TrashedItem>>
        get() = _trashedItems

    private val _filter =
        BehaviorSubject.createDefault(savedState[KEY_FILTER] ?: DEFAULT_FILTER)
    override var filter: TrashedFilter
        get() = _filter.value!!
        set(value) {
            _filter.onNext(value)
        }

    private val disposable = CompositeDisposable()

    init {
        // Create a subscription to recycle bin items state, and chain it
        // from filter type state
        val subscription = _filter
            .switchMap { filter -> repository.getAll(filter) }
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
        // Cancel any observable subscriptions
        if (!disposable.isDisposed) {
            disposable.dispose()
        }

        // Save handled state
        savedState.set(KEY_FILTER,_filter.value)
    }
}