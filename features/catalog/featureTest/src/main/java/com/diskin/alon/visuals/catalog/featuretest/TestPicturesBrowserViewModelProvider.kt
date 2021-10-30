package com.diskin.alon.visuals.catalog.featuretest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.catalog.presentation.interfaces.PictureRepository
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PicturesBrowserViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class TestPicturesBrowserViewModelProvider @Inject constructor(
    private val repositoryProvider: Provider<PictureRepository>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PicturesBrowserViewModelImpl(
            repositoryProvider.get()
        ) as T
    }
}