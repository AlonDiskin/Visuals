package com.diskin.alon.visuals.di.pictures

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.catalog.data.PictureRepositoryImpl
import com.diskin.alon.visuals.catalog.presentation.controller.PicturesBrowserFragment
import com.diskin.alon.visuals.catalog.presentation.interfaces.PictureRepository
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PicturesBrowserViewModel
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PicturesBrowserViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class PicturesBrowserFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun providePhotosViewModel(
            fragment: PicturesBrowserFragment,
            factory: PicturesBrowserViewModelProvider
        ): PicturesBrowserViewModel {
            return ViewModelProvider(fragment,factory).get(PicturesBrowserViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindPhotoRepository(photoRepositoryImpl: PictureRepositoryImpl): PictureRepository
}