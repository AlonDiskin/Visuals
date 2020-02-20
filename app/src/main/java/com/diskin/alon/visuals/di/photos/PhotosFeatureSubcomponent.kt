package com.diskin.alon.visuals.di.photos

import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [PhotosFeatureModule::class])
interface PhotosFeatureSubcomponent : AndroidInjector<PicturesFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<PicturesFragment>
}