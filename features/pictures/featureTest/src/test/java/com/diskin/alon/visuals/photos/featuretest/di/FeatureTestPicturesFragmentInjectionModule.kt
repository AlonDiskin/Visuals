package com.diskin.alon.visuals.photos.featuretest.di

import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [FeatureTestPicturesSubcomponent::class])
abstract class FeatureTestPicturesFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(PicturesFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: FeatureTestPicturesSubcomponent.Factory): AndroidInjector.Factory<*>
}