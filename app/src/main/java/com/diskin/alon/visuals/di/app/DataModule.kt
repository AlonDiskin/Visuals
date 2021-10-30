package com.diskin.alon.visuals.di.app

import android.app.Application
import android.content.ContentResolver
import androidx.room.Room
import com.diskin.alon.common.data.AppDatabase
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.catalog.data.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DataModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideContentResolver(application: Application): ContentResolver {
            return application.contentResolver
        }

        @JvmStatic
        @Singleton
        @Provides
        fun provideDb(app: Application): AppDatabase {
            return Room.databaseBuilder(app,
                AppDatabase::class.java, "visuals-db")
                .build()
        }

        @JvmStatic
        @Singleton
        @Provides
        fun provideTrashedItemDao(db: AppDatabase): TrashedItemDao {
            return db.trashedDao()
        }
    }

    @Binds
    abstract fun bindMediaVisualsProvider(mediaStoreVisualProvider: MediaStoreVisualProvider): DeviceMediaProvider<MediaStoreVisual>

    @Binds
    abstract fun bindPicturesProvider(mediaStorePictureProvider: MediaStorePictureProvider): DeviceMediaProvider<MediaStorePicture>

    @Binds
    abstract fun bindVideoProvider(mediaStoreVideoProvider: MediaStoreVideoProvider): DeviceMediaProvider<MediaStoreVideo>
}