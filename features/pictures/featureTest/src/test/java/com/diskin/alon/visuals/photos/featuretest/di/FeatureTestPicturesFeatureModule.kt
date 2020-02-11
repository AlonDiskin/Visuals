package com.diskin.alon.visuals.photos.featuretest.di

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.photos.data.PictureRepositoryImpl
import com.diskin.alon.visuals.photos.presentation.PictureRepository
import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import com.diskin.alon.visuals.photos.presentation.PicturesViewModel
import com.diskin.alon.visuals.photos.presentation.PicturesViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class FeatureTestPicturesFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun providePhotosViewModel(
            factory: FeatureTestPicturesViewModelProvider,
            fragment: PicturesFragment): PicturesViewModel {

            return ViewModelProvider(fragment,factory).get(PicturesViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindPhotosRepository(photosRepositoryImpl: PictureRepositoryImpl): PictureRepository
}