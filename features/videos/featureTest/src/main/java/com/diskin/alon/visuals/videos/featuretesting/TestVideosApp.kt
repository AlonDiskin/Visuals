package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestVideosApp : DaggerApplication(), TestLifecycleApplication {

    private lateinit var featureTestAppComponent: TestVideosAppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        featureTestAppComponent = DaggerTestVideosAppComponent
            .factory()
            .create(this)

        return featureTestAppComponent
    }

    fun getMockedVideosProvider(): DeviceMediaProvider<MediaStoreVideo> {
        return featureTestAppComponent.getDeviceVideosProvider()
    }

    fun getTestTrashedDao(): TrashedItemDao {
        return featureTestAppComponent.getTestTrashedDao()
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {

    }

    override fun afterTest(method: Method?) {

    }
}