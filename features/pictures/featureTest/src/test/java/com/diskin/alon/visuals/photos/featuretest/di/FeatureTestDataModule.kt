package com.diskin.alon.visuals.photos.featuretest.di

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Module
object FeatureTestDataModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDevicePhotosProvider(): DeviceDataProvider<MediaStorePicture> {
        return mockk {  }
    }
}