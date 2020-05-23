package com.diskin.alon.visuals.di.app

import android.app.Application
import androidx.annotation.VisibleForTesting
import com.diskin.alon.common.data.AppDatabase
import com.diskin.alon.visuals.VisualsApp
import com.diskin.alon.visuals.di.home.MainActivityInjectionModule
import com.diskin.alon.visuals.di.pictures.PicturesBrowserFragmentInjectionModule
import com.diskin.alon.visuals.di.pictures.PictureDetailFragmentInjectionModule
import com.diskin.alon.visuals.di.pictures.PictureViewerActivityInjectionModule
import com.diskin.alon.visuals.di.recyclebin.TrashedItemsFragmentInjectionModule
import com.diskin.alon.visuals.di.videos.VideoDetailFragmentInjectionModule
import com.diskin.alon.visuals.di.videos.VideoDetailActivityInjectionModule
import com.diskin.alon.visuals.di.videos.VideosFragmentInjectionModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    NavigationModule::class,
    DataModule::class,
    MainActivityInjectionModule::class,
    PicturesBrowserFragmentInjectionModule::class,
    VideosFragmentInjectionModule::class,
    VideoDetailActivityInjectionModule::class,
    PictureViewerActivityInjectionModule::class,
    PictureDetailFragmentInjectionModule::class,
    VideoDetailFragmentInjectionModule::class,
    TrashedItemsFragmentInjectionModule::class
])
interface AppComponent : AndroidInjector<VisualsApp> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: Application): AppComponent
    }

    @VisibleForTesting
    fun getAppDatabase(): AppDatabase
}