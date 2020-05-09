package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.visuals.videos.presentation.controller.VideoDetailFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TestVideoDetailFeatureModule::class])
interface TestVideoDetailFeatureSubcomponent : AndroidInjector<VideoDetailFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideoDetailFragment>
}