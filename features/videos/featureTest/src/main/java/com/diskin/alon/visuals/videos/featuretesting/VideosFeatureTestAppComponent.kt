package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    VideosFeatureTestDataModule::class,
    AndroidInjectionModule::class,
    VideosFeatureTestVideosFragmentInjectionModule::class,
    VideosFeaturesTestVideoPlayerActivityInjectionModule::class])
interface VideosFeatureTestAppComponent : AndroidInjector<VideosFeatureTestApp>{

    fun getDeviceVideosProvider(): DeviceDataProvider<MediaStoreVideo>
}