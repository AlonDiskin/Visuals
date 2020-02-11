package com.diskin.alon.visuals.di.app

import android.app.Application
import com.diskin.alon.visuals.VisualsApp
import com.diskin.alon.visuals.di.home.MainActivityInjectionModule
import com.diskin.alon.visuals.di.photos.PhotosFragmentInjectionModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    NavigationModule::class,
    MainActivityInjectionModule::class,
    PhotosFragmentInjectionModule::class
])
interface AppComponent : AndroidInjector<VisualsApp> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: Application): AppComponent
    }
}