package com.diskin.alon.visuals.photos.featuretest

import com.diskin.alon.visuals.photos.presentation.controller.PicturesBrowserFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [TestPicturesBrowserFeatureModule::class])
interface TestPicturesBrowserFeatureSubcomponent : AndroidInjector<PicturesBrowserFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<PicturesBrowserFragment>
}