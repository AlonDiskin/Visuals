package com.diskin.alon.visuals.photos.featuretest.di

import com.diskin.alon.visuals.photos.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object FeatureTestDataModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDevicePhotosProvider(): DeviceDataProvider<MediaStorePicture> {
        return mock {  }
    }
}