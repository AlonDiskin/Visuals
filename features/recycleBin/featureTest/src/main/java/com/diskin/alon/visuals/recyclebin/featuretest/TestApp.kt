package com.diskin.alon.visuals.recyclebin.featuretest

import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestApp : DaggerApplication(), TestLifecycleApplication {
    private lateinit var testAppComponent: TestAppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        testAppComponent = DaggerTestAppComponent.factory().create(this)
        return testAppComponent
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {

    }

    override fun afterTest(method: Method?) {

    }

    fun getTestTrashedItemsDao(): TrashedItemDao {
        return testAppComponent.getTestTrashedItemDao()
    }

    fun getDeviceMediaProvider(): DeviceMediaProvider<MediaStoreVisual> {
        return testAppComponent.getDeviceMediaProvider()
    }
}