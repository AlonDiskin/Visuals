package com.diskin.alon.visuals.videos.featuretesting

import android.app.Application
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    TestVideosDataModule::class,
    AndroidInjectionModule::class,
    TestVideosBrowserFragmentInjectionModule::class,
    TestVideoPlayerActivityInjectionModule::class,
    TestVideoDetailFragmentInjectionModule::class
])
interface TestVideosAppComponent : AndroidInjector<TestVideosApp>{

    @Component.Factory
    interface Builder {
        fun create(@BindsInstance app: Application): TestVideosAppComponent
    }

    fun getDeviceVideosProvider(): DeviceMediaProvider<MediaStoreVideo>

    fun getTestTrashedDao(): TrashedItemDao
}