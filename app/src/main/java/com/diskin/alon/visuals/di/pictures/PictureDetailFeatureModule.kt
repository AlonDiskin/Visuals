package com.diskin.alon.visuals.di.pictures

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.catalog.data.PictureDetailRepositoryImpl
import com.diskin.alon.visuals.catalog.presentation.controller.PictureDetailFragment
import com.diskin.alon.visuals.catalog.presentation.interfaces.PictureDetailRepository
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PictureDetailViewModel
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PictureDetailViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class PictureDetailFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun providePictureDetailViewModel(
            fragment: PictureDetailFragment,
            factory: PictureDetailViewModelProvider
        ): PictureDetailViewModel {
            return ViewModelProvider(fragment,factory).get(PictureDetailViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindPictureDetailRepository(
        pictureDetailRepositoryImpl: PictureDetailRepositoryImpl
    ): PictureDetailRepository
}