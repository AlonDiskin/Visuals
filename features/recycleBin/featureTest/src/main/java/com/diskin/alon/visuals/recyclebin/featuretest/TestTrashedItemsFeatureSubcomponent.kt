package com.diskin.alon.visuals.recyclebin.featuretest

import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashBrowserFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TestTrashedItemsFeatureModule::class])
interface TestTrashedItemsFeatureSubcomponent : AndroidInjector<TrashBrowserFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<TrashBrowserFragment>
}