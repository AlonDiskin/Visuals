package com.diskin.alon.visuals.videos.featuretesting

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
abstract class VideosFeatureTestVideosFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideVideosViewModel(
            factory: VideosFeatureTestVideosViewModelProvider,
            fragment: VideosBrowserFragment
        ): VideosBrowserViewModel {

            return ViewModelProvider(fragment,factory).get(VideosBrowserViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindVideosRepository(videosRepositoryImpl: VideoRepositoryImpl): VideoRepository
}