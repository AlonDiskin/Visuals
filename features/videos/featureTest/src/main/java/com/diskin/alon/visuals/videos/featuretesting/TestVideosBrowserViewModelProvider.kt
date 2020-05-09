package com.diskin.alon.visuals.videos.featuretesting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoRepository
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideosBrowserViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class TestVideosBrowserViewModelProvider @Inject constructor(
    private val repositoryProvider: Provider<VideoRepository>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideosBrowserViewModelImpl(
            repositoryProvider.get()
        ) as T
    }
}