package com.diskin.alon.videos.featuretesting.di

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    FeatureTestDataModule::class,
    AndroidInjectionModule::class,
    FeatureTestVideosFragmentInjectionModule::class])
interface FeatureTestAppComponent : AndroidInjector<FeatureTestApp>{

    fun getDeviceVideosProvider(): DeviceDataProvider<MediaStoreVideo>
}