package com.diskin.alon.visuals.di.videos

import com.diskin.alon.visuals.videos.presentation.controller.VideoDetailActivity
import com.diskin.alon.visuals.videos.presentation.controller.VideoPlayerFragmentsFactory
import com.diskin.alon.visuals.videos.presentation.controller.VideoPlayerFragmentsFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VideoDetailActivityInjectionModule {

    @Binds
    abstract fun bindFragmentsFactory(
        fragmentFactoryImpl: VideoPlayerFragmentsFactoryImpl
    ): VideoPlayerFragmentsFactory

    @ContributesAndroidInjector
    abstract fun videoPlayerActivityAndroidInjector(): VideoDetailActivity
}