package com.diskin.alon.visuals.catalog.featuretest

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.catalog.data.PictureRepositoryImpl
import com.diskin.alon.visuals.catalog.presentation.interfaces.PictureRepository
import com.diskin.alon.visuals.catalog.presentation.controller.PicturesBrowserFragment
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PicturesBrowserViewModel
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PicturesBrowserViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class TestPicturesBrowserFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun providePicturesBrowserViewModel(
            factory: TestPicturesBrowserViewModelProvider,
            fragment: PicturesBrowserFragment
        ): PicturesBrowserViewModel {

            return ViewModelProvider(fragment,factory).get(PicturesBrowserViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindPictureRepository(pictureRepositoryImpl: PictureRepositoryImpl): PictureRepository
}