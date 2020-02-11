package com.diskin.alon.visuals.di.photos

import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [PhotosFeatureSubcomponent::class])
abstract class PhotosFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(PicturesFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: PhotosFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}