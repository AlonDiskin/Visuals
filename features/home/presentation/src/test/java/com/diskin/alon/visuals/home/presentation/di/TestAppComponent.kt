package com.diskin.alon.visuals.home.presentation.di

import com.diskin.alon.visuals.home.presentation.MainActivityTest
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    TestAppModule::class,
    TestMainActivityInjectionModule::class
])
interface TestAppComponent : AndroidInjector<TestApp> {

    fun inject(test: MainActivityTest)
}