package com.diskin.alon.visuals.videos.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource
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

    private val trashVideosSubject = BehaviorSubject.create<Array<Uri>>()

    private val compositeDisposable = CompositeDisposable()

    init {
        // Subscribe to repository videos observable
        val videosSubscription = repository.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { EspressoIdlingResource.increment() }
            .doOnNext { EspressoIdlingResource.decrement() }
            .doFinally { EspressoIdlingResource.decrement() }
            .subscribe({ _videos.value = it },{ _videosUpdateFail.value = it.message!! })

        // Create rx chain for videos trashing
        val trashVideosSubscription = trashVideosSubject
            .concatMap { repository.trash(*it).andThen(Observable.just(Unit)) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { EspressoIdlingResource.increment() }
            .doOnNext { EspressoIdlingResource.decrement() }
            .doFinally { EspressoIdlingResource.decrement() }
            .subscribe({ _videosTrashEvent.value = Event(Status.SUCCESS) },
                { _videosTrashEvent.value = Event(Status.FAILURE) })

        // Add subscriptions to disposable container
        compositeDisposable.addAll(
            videosSubscription,
            trashVideosSubscription
        )
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose of all subscriptions
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun trashVideos(vararg videoUri: Uri) {
        trashVideosSubject.onNext(videoUri.map { it }.toTypedArray())
    }
}