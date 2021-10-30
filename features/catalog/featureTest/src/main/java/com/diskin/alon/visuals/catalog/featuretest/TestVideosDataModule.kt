package com.diskin.alon.visuals.catalog.featuretest

import android.app.Application
import androidx.room.Room
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.catalog.data.MediaStoreVideo
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Module
object TestVideosDataModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDao(app: Application): TrashedItemDao {
        return Room.inMemoryDatabaseBuilder(
            app, TestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
            .trashedDao()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDeviceVideosProvider(): DeviceMediaProvider<MediaStoreVideo> {
        return mockk()
    }
}