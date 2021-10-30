package com.diskin.alon.visuals.catalog.featuretest

import com.diskin.alon.visuals.catalog.presentation.controller.VideosBrowserFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TestVideosBrowserFeatureModule::class])
interface TestVideosBrowserFeatureSubcomponent : AndroidInjector<VideosBrowserFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideosBrowserFragment>
}