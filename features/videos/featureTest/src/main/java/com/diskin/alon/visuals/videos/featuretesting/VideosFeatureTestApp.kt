package com.diskin.alon.visuals.videos.featuretesting

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class VideosFeatureTestApp : DaggerApplication(), TestLifecycleApplication {

    private lateinit var featureTestAppComponent: VideosFeatureTestAppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {

        featureTestAppComponent = DaggerVideosFeatureTestAppComponent.create()

        return featureTestAppComponent
    }

    fun getMockedVideosProvider(): DeviceDataProvider<MediaStoreVideo> {
        return featureTestAppComponent.getDeviceVideosProvider()
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {

    }

    override fun afterTest(method: Method?) {

    }
}