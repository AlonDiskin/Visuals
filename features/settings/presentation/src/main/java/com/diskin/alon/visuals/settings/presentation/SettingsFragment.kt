package com.diskin.alon.visuals.settings.presentation

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat

/**
 * Application preferences UI controller.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    private val sharedPreferenceChangeListener = OnSharedPreferenceChangeListener { prefs, key ->
            val appThemeKey = getString(R.string.pref_theme_key)

            if (key == appThemeKey) {
                val themeDefault = getString(R.string.pref_theme_default_value)
                if (prefs.getString(appThemeKey, themeDefault) == getString(R.string.pref_theme_day_value)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }
}
