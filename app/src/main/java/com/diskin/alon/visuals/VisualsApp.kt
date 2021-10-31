package com.diskin.alon.visuals

import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import com.diskin.alon.visuals.di.app.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class VisualsApp : DaggerApplication() {

    private val appComponent = DaggerAppComponent.factory().create(this)

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

        // Restore night mode according to app preference
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val darkModeKey = getString(R.string.pref_dark_mode_key)
        val darkModeDefault = getString(R.string.pref_dark_mode_default_value)

        when(sp.getString(darkModeKey,darkModeDefault)) {
            "true" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "false" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}