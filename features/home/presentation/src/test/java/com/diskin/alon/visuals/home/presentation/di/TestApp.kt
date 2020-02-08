package com.diskin.alon.visuals.home.presentation.di

import com.diskin.alon.visuals.home.presentation.MainActivityTest
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestApp : DaggerApplication(), TestLifecycleApplication {

    private val testAppComponent = DaggerTestAppComponent.create()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return testAppComponent
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {
        // Inject MainActivityTest with mocked collaborators of MainActivity
        testAppComponent.inject(test as MainActivityTest)
    }

    override fun afterTest(method: Method?) {

    }
}