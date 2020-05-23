package com.diskin.alon.visuals.recyclebin.featuretest

import android.app.Application
import androidx.room.Room
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Module
object TestDataModule {

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
    fun provideDeviceMediaProvider(): DeviceMediaProvider<MediaStoreVisual> {
        return mockk {  }
    }
}