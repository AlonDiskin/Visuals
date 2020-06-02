package com.diskin.alon.visuals.photos.featuretest

import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Module
object TestDataModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDevicePhotosProvider(): DeviceMediaProvider<MediaStorePicture> {
        return mockk {  }
    }
}