package com.diskin.alon.visuals.di

import android.app.Application
import com.diskin.alon.visuals.VisualsApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
AndroidInjectionModule::class,
NavigationModule::class,
MainActivityInjectionModule::class
])
interface AppComponent : AndroidInjector<VisualsApp> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: Application): AppComponent
    }
}