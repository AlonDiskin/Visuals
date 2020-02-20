package com.diskin.alon.visuals.photos.presentation.di

import com.diskin.alon.visuals.photos.presentation.PicturesViewModel
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TestModule {

    @JvmStatic
    @Singleton
    @Provides
    fun providePhotosViewModel(): PicturesViewModel {
        return mock {  }
    }
}