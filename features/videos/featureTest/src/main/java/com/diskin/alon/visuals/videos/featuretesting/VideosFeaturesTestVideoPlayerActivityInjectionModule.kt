package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.visuals.videos.presentation.controller.VideoPlayerActivity
import com.diskin.alon.visuals.videos.presentation.controller.VideoPlayerFragmentsFactory
import com.diskin.alon.visuals.videos.presentation.controller.VideoPlayerFragmentsFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VideosFeaturesTestVideoPlayerActivityInjectionModule {

    @Binds
    abstract fun bindFragmentsFactory(
        fragmentFactoryImpl: VideoPlayerFragmentsFactoryImpl
    ): VideoPlayerFragmentsFactory

    @ContributesAndroidInjector
    abstract fun pictureActivityAndroidInjector(): VideoPlayerActivity
}