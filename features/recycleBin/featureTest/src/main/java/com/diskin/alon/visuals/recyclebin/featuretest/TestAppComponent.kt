package com.diskin.alon.visuals.recyclebin.featuretest

import android.app.Application
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    TestDataModule::class,
    TestTrashedItemsFragmentInjectionModule::class
])
interface TestAppComponent : AndroidInjector<TestApp>{

    @Component.Factory
    interface Builder {
        fun create(@BindsInstance app: Application): TestAppComponent
    }

    fun getTestTrashedItemDao(): TrashedItemDao

    fun getDeviceMediaProvider(): DeviceMediaProvider<MediaStoreVisual>
}