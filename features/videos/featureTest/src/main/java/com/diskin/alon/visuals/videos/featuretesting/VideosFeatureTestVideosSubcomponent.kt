package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.visuals.videos.presentation.controller.VideosBrowserFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [VideosFeatureTestVideosFeatureModule::class])
interface VideosFeatureTestVideosSubcomponent : AndroidInjector<VideosBrowserFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideosBrowserFragment>
}