package com.diskin.alon.visuals.di.pictures

import com.diskin.alon.visuals.photos.presentation.controller.PicturesBrowserFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [PicturesBrowserFeatureModule::class])
interface PicturesBrowserFeatureSubcomponent : AndroidInjector<PicturesBrowserFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<PicturesBrowserFragment>
}