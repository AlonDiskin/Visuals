package com.diskin.alon.visuals.recyclebin.featuretest

import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashBrowserFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [TestTrashedItemsFeatureSubcomponent::class])
abstract class TestTrashedItemsFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(TrashBrowserFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: TestTrashedItemsFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}