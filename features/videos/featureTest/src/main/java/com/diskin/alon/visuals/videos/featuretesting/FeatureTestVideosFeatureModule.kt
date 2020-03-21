package com.diskin.alon.visuals.videos.featuretesting

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
abstract class FeatureTestVideosFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideVideosViewModel(
            factory: FeatureTestVideosViewModelProvider,
            fragment: VideosFragment
        ): VideosViewModel {

            return ViewModelProvider(fragment,factory).get(VideosViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindVideosRepository(videosRepositoryImpl: VideoRepositoryImpl): VideoRepository
}