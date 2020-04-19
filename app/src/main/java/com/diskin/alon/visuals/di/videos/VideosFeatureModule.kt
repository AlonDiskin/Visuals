package com.diskin.alon.visuals.di.videos

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.videos.data.VideoRepositoryImpl
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoRepository
import com.diskin.alon.visuals.videos.presentation.controller.VideosBrowserFragment
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideosBrowserViewModel
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideosBrowserViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class VideosFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideVideosViewModel(
            fragment: VideosBrowserFragment,
            factory: VideosViewModelProvider
        ): VideosBrowserViewModel {
            return ViewModelProvider(fragment,factory).get(VideosBrowserViewModelImpl::class.java)
        }
    }


    @Binds
    abstract fun bindVideoRepository(videoRepositoryImpl: VideoRepositoryImpl): VideoRepository
}