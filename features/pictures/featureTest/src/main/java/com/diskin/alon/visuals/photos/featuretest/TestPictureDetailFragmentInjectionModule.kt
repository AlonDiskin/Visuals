package com.diskin.alon.visuals.photos.featuretest

import com.diskin.alon.visuals.photos.presentation.controller.PictureDetailFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [TestPictureDetailFeatureSubcomponent::class])
abstract class TestPictureDetailFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(PictureDetailFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: TestPictureDetailFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}