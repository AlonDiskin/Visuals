package com.diskin.alon.visuals.di.videos

import com.diskin.alon.visuals.catalog.presentation.controller.VideosBrowserFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [VideosFeatureModule::class])
interface VideosFeatureSubcomponent : AndroidInjector<VideosBrowserFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideosBrowserFragment>
}