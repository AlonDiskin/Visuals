package com.diskin.alon.visuals.videos.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoRepository
import com.diskin.alon.visuals.videos.presentation.model.Video
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideosBrowserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    private val compositeDisposable = CompositeDisposable()

    init {
        // Subscribe to repository videos observable
        val videosSubscription = repository.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { EspressoIdlingResource.increment() }
            .doOnNext { EspressoIdlingResource.decrement() }
            .doFinally { EspressoIdlingResource.decrement() }
            .subscribe(
                this::handleVideosUpdate,
                this::handleVideosUpdateError)

        // Add subscription to disposable container
        compositeDisposable.add(videosSubscription)
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose of all subscriptions
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private fun handleVideosUpdate(list: List<Video>) {
        _videos.value = list
    }

    private fun handleVideosUpdateError(throwable: Throwable) {
        _videosUpdateFail.value = throwable.message!!
    }
}