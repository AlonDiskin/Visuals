package com.diskin.alon.visuals.photos.featuretest

import com.diskin.alon.visuals.photos.presentation.controller.PictureDetailFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TestPictureDetailFeatureModule::class])
interface TestPictureDetailFeatureSubcomponent : AndroidInjector<PictureDetailFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<PictureDetailFragment>
}