package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Module
object TestVideosDataModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDeviceVideosProvider(): DeviceDataProvider<MediaStoreVideo> {
        return mockk()
    }
}