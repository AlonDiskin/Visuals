package com.diskin.alon.videos.featuretesting.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.videos.presentation.VideoRepository
import com.diskin.alon.visuals.videos.presentation.VideosViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class FeatureTestVideosViewModelProvider @Inject constructor(
    private val repositoryProvider: Provider<VideoRepository>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideosViewModelImpl(repositoryProvider.get()) as T
    }
}