package com.diskin.alon.visuals.di.pictures

import com.diskin.alon.visuals.catalog.presentation.controller.PicturesBrowserFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [PicturesBrowserFeatureSubcomponent::class])
abstract class PicturesBrowserFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(PicturesBrowserFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: PicturesBrowserFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}