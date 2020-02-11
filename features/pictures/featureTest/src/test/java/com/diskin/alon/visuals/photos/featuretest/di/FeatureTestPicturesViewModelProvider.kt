package com.diskin.alon.visuals.photos.featuretest.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.photos.presentation.PictureRepository
import com.diskin.alon.visuals.photos.presentation.PicturesViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class FeatureTestPicturesViewModelProvider @Inject constructor(
    private val repositoryProvider: Provider<PictureRepository>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PicturesViewModelImpl(repositoryProvider.get()) as T
    }
}