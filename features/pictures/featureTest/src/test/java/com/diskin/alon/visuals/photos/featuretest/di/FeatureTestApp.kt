package com.diskin.alon.visuals.photos.featuretest.di

import com.diskin.alon.visuals.photos.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
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

    fun getMockedPicturesProvider(): DeviceDataProvider<MediaStorePicture> {
        return featureTestAppComponent.getDevicePicturesProvider()
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {

    }

    override fun afterTest(method: Method?) {

    }
}