package com.diskin.alon.visuals.di.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.photos.presentation.PictureRepository
import com.diskin.alon.visuals.photos.presentation.PicturesViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class PhotosViewModelProvider @Inject constructor(
    private val repositoryProvider: Provider<PictureRepository>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PicturesViewModelImpl(repositoryProvider.get()) as T
    }
}