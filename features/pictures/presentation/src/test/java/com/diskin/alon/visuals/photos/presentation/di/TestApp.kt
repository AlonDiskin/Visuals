package com.diskin.alon.visuals.photos.presentation.di

import com.diskin.alon.visuals.photos.presentation.PicturesFragmentTest
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestApp : DaggerApplication(), TestLifecycleApplication {

    private lateinit var testAppComponent: TestAppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        testAppComponent = DaggerTestAppComponent.create()

        return testAppComponent
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {
        testAppComponent.inject(test as PicturesFragmentTest)
    }

    override fun afterTest(method: Method?) {

    }
}