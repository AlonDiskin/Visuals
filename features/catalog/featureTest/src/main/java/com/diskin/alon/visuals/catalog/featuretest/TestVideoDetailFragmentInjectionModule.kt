package com.diskin.alon.visuals.catalog.featuretest

import com.diskin.alon.visuals.catalog.presentation.controller.VideoDetailFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [TestVideoDetailFeatureSubcomponent::class])
abstract class TestVideoDetailFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(VideoDetailFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: TestVideoDetailFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}