package com.diskin.alon.visuals.catalog.featuretest

import com.diskin.alon.visuals.catalog.presentation.controller.VideoDetailFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TestVideoDetailFeatureModule::class])
interface TestVideoDetailFeatureSubcomponent : AndroidInjector<VideoDetailFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideoDetailFragment>
}