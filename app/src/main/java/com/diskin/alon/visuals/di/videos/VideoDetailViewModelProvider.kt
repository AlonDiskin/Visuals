package com.diskin.alon.visuals.di.videos

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.videos.presentation.controller.VideoDetailFragment
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoDetailRepository
import com.diskin.alon.visuals.videos.presentation.util.VideoDetailMapper
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideoDetailViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class VideoDetailViewModelProvider @Inject constructor(
    fragment: VideoDetailFragment,
    private val repositoryProvider: Provider<VideoDetailRepository>,
    private val mapperProvider: Provider<VideoDetailMapper>
): AbstractSavedStateViewModelFactory(fragment,fragment.arguments) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return VideoDetailViewModelImpl(
            repositoryProvider.get(),
            handle,
            mapperProvider.get()
        ) as T
    }
}