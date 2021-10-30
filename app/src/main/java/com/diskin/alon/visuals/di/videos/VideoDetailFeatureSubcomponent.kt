package com.diskin.alon.visuals.di.videos

import com.diskin.alon.visuals.catalog.presentation.controller.VideoDetailFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [VideoDetailFeatureModule::class])
interface VideoDetailFeatureSubcomponent : AndroidInjector<VideoDetailFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<VideoDetailFragment>
}