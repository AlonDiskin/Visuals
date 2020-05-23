package com.diskin.alon.visuals.di.recyclebin

import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TrashedItemsFeatureModule::class])
interface TrashedItemsFeatureSubcomponent : AndroidInjector<TrashedItemsFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<TrashedItemsFragment>
}