package com.diskin.alon.visuals.di

import com.diskin.alon.visuals.AppNavigator
import com.diskin.alon.visuals.home.presentation.MainNavigator
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class NavigationModule {

    @Singleton
    @Binds
    abstract fun bindMainNavigator(appNavigator: AppNavigator): MainNavigator
}