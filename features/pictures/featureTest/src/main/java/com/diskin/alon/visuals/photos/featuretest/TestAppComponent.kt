package com.diskin.alon.visuals.photos.featuretest

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
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

    fun getDevicePicturesProvider(): DeviceDataProvider<MediaStorePicture>
}