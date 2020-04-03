package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.visuals.videos.presentation.VideosFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [FeatureTestVideosFeatureModule::class])
interface FeatureTestVideosSubcomponent : AndroidInjector<VideosFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideosFragment>
}