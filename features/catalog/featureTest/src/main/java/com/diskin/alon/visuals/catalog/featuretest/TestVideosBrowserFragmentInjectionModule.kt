package com.diskin.alon.visuals.catalog.featuretest

import com.diskin.alon.visuals.catalog.presentation.controller.VideosBrowserFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [TestVideosBrowserFeatureSubcomponent::class])
abstract class TestVideosBrowserFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(VideosBrowserFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: TestVideosBrowserFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}