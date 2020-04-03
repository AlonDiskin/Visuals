package com.diskin.alon.visuals.di.app

import android.app.Application
import android.content.ContentResolver
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.data.MediaStorePictureProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.data.MediaStoreVideoProvider
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class DataModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideContentResolver(application: Application): ContentResolver {
            return application.contentResolver
        }

    }

    @Binds
    abstract fun bindPicturesProvider(mediaStorePictureProvider: MediaStorePictureProvider): DeviceDataProvider<MediaStorePicture>

    @Binds
    abstract fun bindVideoProvider(mediaStoreVideoProvider: MediaStoreVideoProvider): DeviceDataProvider<MediaStoreVideo>
}