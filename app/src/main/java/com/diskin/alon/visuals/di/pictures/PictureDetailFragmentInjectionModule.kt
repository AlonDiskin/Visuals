package com.diskin.alon.visuals.di.pictures

import com.diskin.alon.visuals.catalog.presentation.controller.PictureDetailFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [PictureDetailFeatureSubcomponent::class])
abstract class PictureDetailFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(PictureDetailFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: PictureDetailFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}