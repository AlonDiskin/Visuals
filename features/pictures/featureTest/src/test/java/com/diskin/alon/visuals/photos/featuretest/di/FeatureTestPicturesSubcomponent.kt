package com.diskin.alon.visuals.photos.featuretest.di

import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [FeatureTestPicturesFeatureModule::class])
interface FeatureTestPicturesSubcomponent : AndroidInjector<PicturesFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<PicturesFragment>
}