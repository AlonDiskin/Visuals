package com.diskin.alon.visuals.di.recyclebin

import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [TrashedItemsFeatureSubcomponent::class])
abstract class TrashedItemsFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(TrashedItemsFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: TrashedItemsFeatureSubcomponent.Factory): AndroidInjector.Factory<*>
}