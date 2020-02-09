package com.diskin.alon.visuals

import com.diskin.alon.visuals.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class VisualsApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}