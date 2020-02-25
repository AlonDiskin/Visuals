package com.diskin.alon.visuals.photos.featuretest.di

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    FeatureTestDataModule::class,
    AndroidInjectionModule::class,
    FeatureTestPicturesFragmentInjectionModule::class])
interface FeatureTestAppComponent : AndroidInjector<FeatureTestApp>{

    fun getDevicePicturesProvider(): DeviceDataProvider<MediaStorePicture>
}