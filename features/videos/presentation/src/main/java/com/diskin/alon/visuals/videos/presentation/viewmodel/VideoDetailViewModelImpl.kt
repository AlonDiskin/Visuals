package com.diskin.alon.visuals.videos.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoDetailRepository
import com.diskin.alon.visuals.videos.presentation.model.VideoDetail
import com.diskin.alon.visuals.videos.presentation.model.VideoInfoError
import com.diskin.alon.visuals.videos.presentation.util.VideoDetailMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable

class VideoDetailViewModelImpl(
    repository: VideoDetailRepository,
    savedStateHandle: SavedStateHandle,
    mapper: VideoDetailMapper
) : ViewModel(), VideoDetailViewModel {

    companion object {
        const val VID_URI = "picture detail uri"
    }

    private val _videoDetail = MutableLiveData<VideoDetail>()
    override val videoDetail: LiveData<VideoDetail>
        get() = _videoDetail

    private val _videoInfoError = SingleLiveEvent<VideoInfoError>()
    override val videoInfoError: LiveEvent<VideoInfoError>
        get() = _videoInfoError

    private val disposable = SerialDisposable()

    init {
        val subscription = repository.get(savedStateHandle[VID_URI]!!)
            .observeOn(AndroidSchedulers.mainThread())
            .map { dto -> mapper.mapDetail(dto) }
            .subscribe ({
                _videoDetail.value = it
            },{
                _videoInfoError.value = VideoInfoError(it.message ?: "")
            })

        disposable.set(subscription)
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}