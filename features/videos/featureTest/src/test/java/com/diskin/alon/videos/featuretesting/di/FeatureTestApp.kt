package com.diskin.alon.videos.featuretesting.di

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class FeatureTestApp : DaggerApplication(), TestLifecycleApplication {

    private lateinit var featureTestAppComponent: FeatureTestAppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        featureTestAppComponent = DaggerFeatureTestAppComponent.create()

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