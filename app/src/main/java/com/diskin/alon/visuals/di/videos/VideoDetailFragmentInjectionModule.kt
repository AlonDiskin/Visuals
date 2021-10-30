package com.diskin.alon.visuals.di.videos

import com.diskin.alon.visuals.catalog.presentation.controller.VideoDetailFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [VideoDetailFeatureSubcomponent::class])
abstract class VideoDetailFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(VideoDetailFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: VideoDetailFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}