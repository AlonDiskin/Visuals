package com.diskin.alon.visuals.photos.featuretest

import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestApp : DaggerApplication(), TestLifecycleApplication {

    private val featureTestAppComponent: TestAppComponent
            = DaggerTestAppComponent.create()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return featureTestAppComponent
    }

    fun getMockedPicturesProvider(): DeviceMediaProvider<MediaStorePicture> {
        return featureTestAppComponent.getDevicePicturesProvider()
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {

    }

    override fun afterTest(method: Method?) {

    }
}