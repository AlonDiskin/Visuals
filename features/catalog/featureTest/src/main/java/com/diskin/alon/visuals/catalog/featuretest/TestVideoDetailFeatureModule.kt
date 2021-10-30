package com.diskin.alon.visuals.catalog.featuretest

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.catalog.data.VideoDetailRepositoryImpl
import com.diskin.alon.visuals.catalog.presentation.controller.VideoDetailFragment
import com.diskin.alon.visuals.catalog.presentation.interfaces.VideoDetailRepository
import com.diskin.alon.visuals.catalog.presentation.viewmodel.VideoDetailViewModel
import com.diskin.alon.visuals.catalog.presentation.viewmodel.VideoDetailViewModelImpl
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