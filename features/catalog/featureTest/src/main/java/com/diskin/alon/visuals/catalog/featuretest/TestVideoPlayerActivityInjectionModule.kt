package com.diskin.alon.visuals.catalog.featuretest

import com.diskin.alon.visuals.catalog.presentation.controller.VideoDetailActivity
import com.diskin.alon.visuals.catalog.presentation.controller.VideoPlayerFragmentsFactory
import com.diskin.alon.visuals.catalog.presentation.controller.VideoPlayerFragmentsFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TestVideoPlayerActivityInjectionModule {

    @Binds
    abstract fun bindFragmentsFactory(
        fragmentFactoryImpl: VideoPlayerFragmentsFactoryImpl
    ): VideoPlayerFragmentsFactory

    @ContributesAndroidInjector
    abstract fun pictureActivityAndroidInjector(): VideoDetailActivity
}