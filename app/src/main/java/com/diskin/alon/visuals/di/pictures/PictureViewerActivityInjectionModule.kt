package com.diskin.alon.visuals.di.pictures

import com.diskin.alon.visuals.photos.presentation.controller.PictureViewerActivity
import com.diskin.alon.visuals.photos.presentation.controller.PictureViewerFragmentFactory
import com.diskin.alon.visuals.photos.presentation.controller.PictureViewerFragmentFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PictureViewerActivityInjectionModule {

    @Binds
    abstract fun bindFragmentsFactory(
        fragmentFactoryImpl: PictureViewerFragmentFactoryImpl
    ): PictureViewerFragmentFactory

    @ContributesAndroidInjector
    abstract fun pictureDetailActivityAndroidInjector(): PictureViewerActivity
}