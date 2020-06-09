package com.diskin.alon.visuals.videos.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.Event.Status
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoRepository
import com.diskin.alon.visuals.videos.presentation.model.Video
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class VideosBrowserViewModelImpl @Inject constructor(
    repository: VideoRepository
) : ViewModel() ,
    VideosBrowserViewModel {

    private val _videos = MutableLiveData<List<Video>>()
    override val videos: LiveData<List<Video>>
        get() = _videos

    private val _videosUpdateFail = SingleLiveEvent<String>()
    override val videosUpdateFail: LiveEvent<String>
        get() = _videosUpdateFail

    private val _videosTrashEvent = SingleLiveEvent<Event>()
    override val videosTrashedEvent: LiveEvent<Event>
        get() = _videosTrashEvent

    private val _videosTrashUndoEvent = SingleLiveEvent<Event>()
    override val videosTrashUndoEvent: LiveEvent<Event>
        get() = _videosTrashUndoEvent

    private val removeFromTrashSubject = BehaviorSubject.create<List<Uri>>()
    private val trashVideosSubject = BehaviorSubject.create<List<Uri>>()
    private val compositeDisposable = CompositeDisposable()
    private val lastTrashedCache = mutableListOf<Uri>()

    init {
        // Subscribe to repository videos observable
        val videosSubscription = repository.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _videos.value = it
            },
                {
                    _videosUpdateFail.value = it.message!!
                })

        // Create rx chain for videos trashing
        val trashVideosSubscription = trashVideosSubject
            .concatMap { repository.trash(it).toObservable() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _videosTrashEvent.value = Event(Status.SUCCESS)
                lastTrashedCache.clear()
                lastTrashedCache.addAll(it) },
                {
                    _videosTrashEvent.value = Event(Status.FAILURE)
                })

        // Create rx chain for videos restoring from trash
        val restoreTrashedSubscription = removeFromTrashSubject
            .concatMap { repository.restoreFromTrash(it).andThen(Observable.just(Unit)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _videosTrashUndoEvent.value = Event(Status.SUCCESS)
            },
                {
                    _videosTrashUndoEvent.value = Event(Status.FAILURE)
                })

        // Add subscriptions to disposable container
        compositeDisposable.addAll(
            videosSubscription,
            trashVideosSubscription,
            restoreTrashedSubscription
        )
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose of all subscriptions
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun trashVideos(videosUri: List<Uri>) {
        trashVideosSubject.onNext(videosUri.map { it })
    }

    override fun undoLastTrash() {
        if (lastTrashedCache.isNotEmpty()) {
            removeFromTrashSubject.onNext(lastTrashedCache)
        }
    }
}