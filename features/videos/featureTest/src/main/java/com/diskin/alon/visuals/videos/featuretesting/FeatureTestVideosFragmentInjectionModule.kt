package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.visuals.videos.presentation.VideosFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [FeatureTestVideosSubcomponent::class])
abstract class FeatureTestVideosFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(VideosFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: FeatureTestVideosSubcomponent.Factory): AndroidInjector.Factory<*>
}