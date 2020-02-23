package com.diskin.alon.visuals.photos.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PicturesViewModelImpl @Inject constructor(
    repository: PictureRepository
) : ViewModel(), PicturesViewModel {

    private val _photos = MutableLiveData<List<Picture>>()
    override val photos: LiveData<List<Picture>>
        get() = _photos

    private val _photosUpdateError = SingleLiveEvent<String>()
    override val photosUpdateError: LiveEvent<String>
        get() = _photosUpdateError

    private val compositeDisposable = CompositeDisposable()

    init {
        // Subscribe to repository pictures observable
        val photosSubscription = repository.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { EspressoIdlingResource.increment() }
            .doOnNext { EspressoIdlingResource.decrement() }
            .doFinally { EspressoIdlingResource.decrement() }
            .subscribe(this::handlePhotosUpdate,
                this::handlePhotosUpdateError)

        // Add subscription to disposable container
        compositeDisposable.add(photosSubscription)
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose of all subscriptions
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private fun handlePhotosUpdate(photos: List<Picture>) {
        _photos.value = photos
    }

    private fun handlePhotosUpdateError(throwable: Throwable) {
        _photosUpdateError.value = throwable.message!!
    }
}