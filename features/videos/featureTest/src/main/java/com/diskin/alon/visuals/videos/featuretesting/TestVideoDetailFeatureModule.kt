package com.diskin.alon.visuals.videos.featuretesting

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.videos.data.VideoDetailRepositoryImpl
import com.diskin.alon.visuals.videos.presentation.controller.VideoDetailFragment
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoDetailRepository
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideoDetailViewModel
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideoDetailViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class TestVideoDetailFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun providePictureDetailViewModel(
            fragment: VideoDetailFragment,
            factory: TestVideoDetailViewModelProvider
        ): VideoDetailViewModel {
            return ViewModelProvider(fragment,factory).get(VideoDetailViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindRepository(repository: VideoDetailRepositoryImpl): VideoDetailRepository
}