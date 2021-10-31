package com.diskin.alon.visuals.home.presentation

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val sp: SharedPreferences,
    private val resources: Resources
) {

    fun isDarkModeEnabled(): Boolean {
        return when(AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun setDarkMode(enable: Boolean) {
        val darkModeKey = resources.getString(R.string.pref_dark_mode_key)

        sp.edit().putString(darkModeKey,enable.toString()).apply()
        when(enable) {
            true -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            false -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}