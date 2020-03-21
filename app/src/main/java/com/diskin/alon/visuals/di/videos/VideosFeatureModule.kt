package com.diskin.alon.visuals.di.videos

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.videos.data.VideoRepositoryImpl
import com.diskin.alon.visuals.videos.presentation.VideoRepository
import com.diskin.alon.visuals.videos.presentation.VideosFragment
import com.diskin.alon.visuals.videos.presentation.VideosViewModel
import com.diskin.alon.visuals.videos.presentation.VideosViewModelImpl
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
            fragment: VideosFragment,
            factory: VideosViewModelProvider
        ): VideosViewModel {
            return ViewModelProvider(fragment,factory).get(VideosViewModelImpl::class.java)
        }
    }


    @Binds
    abstract fun bindVideoRepository(videoRepositoryImpl: VideoRepositoryImpl): VideoRepository
}