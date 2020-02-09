package com.diskin.alon.visuals.di

import com.diskin.alon.visuals.home.presentation.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityInjectionModule {

    @ContributesAndroidInjector
    abstract fun contributeYourAndroidInjector(): MainActivity
}