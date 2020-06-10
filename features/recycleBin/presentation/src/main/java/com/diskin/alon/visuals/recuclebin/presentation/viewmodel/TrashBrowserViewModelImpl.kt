package com.diskin.alon.visuals.recuclebin.presentation.viewmodel

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.Event.Status
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.recuclebin.presentation.interfaces.TrashItemRepository
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashedFilter
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class TrashBrowserViewModelImpl(
    repository: TrashItemRepository,
    private val savedState: SavedStateHandle
) : ViewModel(),
    TrashBrowserViewModel {

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        val DEFAULT_FILTER = TrashedFilter.ALL
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        const val KEY_FILTER = "filter"
    }

    private val _trashItems = MutableLiveData<List<TrashItem>>()
    override val trashItems: LiveData<List<TrashItem>>
        get() = _trashItems

    private val _restoreEvent = SingleLiveEvent<Event>()
    override val restoreEvent: LiveEvent<Event>
        get() = _restoreEvent

    private val _restoreAllEvent = SingleLiveEvent<Event>()
    override val restoreAllEvent: LiveEvent<Event>
        get() = _restoreAllEvent

    private val _filter =
        BehaviorSubject.createDefault(savedState[KEY_FILTER] ?: DEFAULT_FILTER)
    override var filter: TrashedFilter
        get() = _filter.value!!
        set(value) {
            _filter.onNext(value)
        }

    private val restoreItemsSubject = BehaviorSubject.create<List<Uri>>()
    private val restoreAllSubject = BehaviorSubject.create<Unit>()
    private val disposable = CompositeDisposable()

    init {
        // Create a subscription to recycle bin items state, and chain it
        // from filter type state
        val trashItemsSubscription = _filter
            .switchMap { filter -> repository.getAll(filter) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ _trashItems.value = it },{ it.printStackTrace() })

        val restoreTrashedSubscription = restoreItemsSubject
            .concatMap { repository.restore(it).andThen(Observable.just(Unit)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { _restoreEvent.value = Event(Status.SUCCESS) },
                { _restoreEvent.value = Event(Status.FAILURE) })

        val restoreAllSubscription = restoreAllSubject
            .switchMap { repository.restoreAll().andThen(Observable.just(Unit)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({}, { _restoreAllEvent.value = Event(Status.FAILURE) })

        disposable.addAll(
            trashItemsSubscription,
            restoreTrashedSubscription,
            restoreAllSubscription
        )
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

    override fun restore(itemsUri: List<Uri>) {
        restoreItemsSubject.onNext(itemsUri)
    }

    override fun restoreAll() {
        restoreAllSubject.onNext(Unit)
    }
}