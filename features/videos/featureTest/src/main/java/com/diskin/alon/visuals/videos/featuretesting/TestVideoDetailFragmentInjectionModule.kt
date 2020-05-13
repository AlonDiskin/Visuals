package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.visuals.videos.presentation.controller.VideoDetailFragment
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