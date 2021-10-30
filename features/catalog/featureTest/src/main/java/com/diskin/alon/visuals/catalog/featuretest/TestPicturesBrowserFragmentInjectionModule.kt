package com.diskin.alon.visuals.catalog.featuretest

import com.diskin.alon.visuals.catalog.presentation.controller.PicturesBrowserFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [TestPicturesBrowserFeatureSubcomponent::class])
abstract class TestPicturesBrowserFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(PicturesBrowserFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: TestPicturesBrowserFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}