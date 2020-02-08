package com.diskin.alon.visuals.home.presentation.di

import com.diskin.alon.visuals.home.presentation.MainNavigator
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TestAppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideMainNavigator(): MainNavigator {
        return mock {  }
    }
}