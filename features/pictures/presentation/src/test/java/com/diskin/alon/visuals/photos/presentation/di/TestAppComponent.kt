package com.diskin.alon.visuals.photos.presentation.di

import com.diskin.alon.visuals.photos.presentation.PicturesFragmentTest
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [TestPicturesFragmentInjectionModule::class,TestModule::class])
interface TestAppComponent : AndroidInjector<TestApp> {

    fun inject(test: PicturesFragmentTest)
}