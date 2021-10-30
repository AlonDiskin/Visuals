package com.diskin.alon.visuals.di.recyclebin

import com.diskin.alon.visuals.catalog.presentation.controller.TrashBrowserFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TrashedItemsFeatureModule::class])
interface TrashedItemsFeatureSubcomponent : AndroidInjector<TrashBrowserFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<TrashBrowserFragment>
}