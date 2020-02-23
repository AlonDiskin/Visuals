package com.diskin.alon.videos.featuretesting.di

import com.diskin.alon.visuals.videos.presentation.VideosFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [FeatureTestVideosFeatureModule::class])
interface FeatureTestVideosSubcomponent : AndroidInjector<VideosFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideosFragment>
}