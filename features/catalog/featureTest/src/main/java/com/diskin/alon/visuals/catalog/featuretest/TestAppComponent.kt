package com.diskin.alon.visuals.catalog.featuretest

import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.catalog.data.MediaStorePicture
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    TestDataModule::class,
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    TestPicturesBrowserFragmentInjectionModule::class,
    TestPictureDetailFragmentInjectionModule::class,
    TestPictureViewerActivityInjectionModule::class])
interface TestAppComponent : AndroidInjector<TestApp>{

    fun getDevicePicturesProvider(): DeviceMediaProvider<MediaStorePicture>
}