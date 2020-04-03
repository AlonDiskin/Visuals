package com.diskin.alon.visuals.di.pictures

import com.diskin.alon.visuals.photos.presentation.controller.PictureDetailFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [PictureDetailFeatureModule::class])
interface PictureDetailFeatureSubcomponent : AndroidInjector<PictureDetailFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<PictureDetailFragment>
}