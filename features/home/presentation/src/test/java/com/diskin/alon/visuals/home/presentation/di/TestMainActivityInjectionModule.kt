package com.diskin.alon.visuals.home.presentation.di

import com.diskin.alon.visuals.home.presentation.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class TestMainActivityInjectionModule {

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeYourAndroidInjector(): MainActivity
}