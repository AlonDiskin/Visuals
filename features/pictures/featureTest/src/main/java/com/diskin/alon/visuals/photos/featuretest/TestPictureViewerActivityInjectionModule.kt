package com.diskin.alon.visuals.photos.featuretest

import com.diskin.alon.visuals.photos.presentation.controller.PictureViewerActivity
import com.diskin.alon.visuals.photos.presentation.controller.PictureViewerFragmentFactory
import com.diskin.alon.visuals.photos.presentation.controller.PictureViewerFragmentFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TestPictureViewerActivityInjectionModule {

    @Binds
    abstract fun bindFragmentsFactory(
        fragmentFactoryImpl: PictureViewerFragmentFactoryImpl
    ): PictureViewerFragmentFactory

    @ContributesAndroidInjector
    abstract fun pictureDetailActivityAndroidInjector(): PictureViewerActivity
}