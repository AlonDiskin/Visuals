package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.visuals.videos.presentation.controller.VideosBrowserFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [VideosFeatureTestVideosSubcomponent::class])
abstract class VideosFeatureTestVideosFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(VideosBrowserFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: VideosFeatureTestVideosSubcomponent.Factory): AndroidInjector.Factory<*>
}