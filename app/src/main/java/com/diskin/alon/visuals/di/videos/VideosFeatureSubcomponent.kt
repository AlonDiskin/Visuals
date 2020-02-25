package com.diskin.alon.visuals.di.videos

import com.diskin.alon.visuals.videos.presentation.VideosFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [VideosFeatureModule::class])
interface VideosFeatureSubcomponent : AndroidInjector<VideosFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideosFragment>
}