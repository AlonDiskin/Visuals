package com.diskin.alon.visuals.di.videos

import com.diskin.alon.visuals.videos.presentation.VideosFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [VideosFeatureSubcomponent::class])
abstract class VideosFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(VideosFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: VideosFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}