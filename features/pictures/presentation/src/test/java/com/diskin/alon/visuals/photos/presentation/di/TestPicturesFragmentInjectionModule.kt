package com.diskin.alon.visuals.photos.presentation.di

import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [AndroidSupportInjectionModule::class])
abstract class TestPicturesFragmentInjectionModule {
    @ContributesAndroidInjector
    abstract fun contributeFragmentInjector(): PicturesFragment

}