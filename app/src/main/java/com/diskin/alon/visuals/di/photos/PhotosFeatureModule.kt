package com.diskin.alon.visuals.di.photos

import android.app.Application
import android.content.ContentResolver
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.photos.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.data.MediaStorePictureProvider
import com.diskin.alon.visuals.photos.data.PictureRepositoryImpl
import com.diskin.alon.visuals.photos.presentation.PictureRepository
import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import com.diskin.alon.visuals.photos.presentation.PicturesViewModel
import com.diskin.alon.visuals.photos.presentation.PicturesViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class PhotosFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideContentResolver(application: Application): ContentResolver {
            return application.contentResolver
        }

        @JvmStatic
        @Provides
        fun providePhotosViewModel(
            fragment: PicturesFragment,
            factory: PhotosViewModelProvider
        ): PicturesViewModel {
            return ViewModelProvider(fragment,factory).get(PicturesViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindPhotosProvider(mediaStorePhotoProvider: MediaStorePictureProvider): DeviceDataProvider<MediaStorePicture>

    @Binds
    abstract fun bindPhotoRepository(photoRepositoryImpl: PictureRepositoryImpl): PictureRepository
}