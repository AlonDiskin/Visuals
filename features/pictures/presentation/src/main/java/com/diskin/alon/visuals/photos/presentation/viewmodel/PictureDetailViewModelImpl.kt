package com.diskin.alon.visuals.photos.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.photos.presentation.interfaces.PictureDetailRepository
import com.diskin.alon.visuals.photos.presentation.model.PictureDetail
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable

class PictureDetailViewModelImpl(
    repository: PictureDetailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), PictureDetailViewModel {

    companion object {
        const val KEY_PIC_URI = "picture detail uri"
    }

    private val _pictureDetail = MutableLiveData<PictureDetail>()
    override val pictureDetail: LiveData<PictureDetail>
        get() = _pictureDetail

    private val _pictureError = SingleLiveEvent<Event>()
    override val pictureError: LiveEvent<Event>
        get() = _pictureError

    private val disposable = SerialDisposable()

    init {
        val subscription = repository.get(savedStateHandle.get<Uri>(KEY_PIC_URI)!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handlePictureLoadingSuccess,::handlePictureLoadingError)

        disposable.set(subscription)
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    private fun handlePictureLoadingSuccess(pictureDetail: PictureDetail) {
        _pictureDetail.value = pictureDetail
    }

    private fun handlePictureLoadingError(throwable: Throwable) {
        _pictureError.value = Event()
    }
}