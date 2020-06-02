package com.diskin.alon.visuals.recyclebin.featuretest

import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TestTrashedItemsFeatureModule::class])
interface TestTrashedItemsFeatureSubcomponent : AndroidInjector<TrashedItemsFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<TrashedItemsFragment>
}