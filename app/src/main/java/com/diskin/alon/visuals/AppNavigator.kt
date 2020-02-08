package com.diskin.alon.visuals

import android.app.Application
import android.content.Intent
import com.diskin.alon.visuals.home.presentation.MainNavigator
import com.diskin.alon.visuals.settings.presentation.SettingsActivity
import javax.inject.Inject

/**
 * Applies navigation to app destinations.
 */
class AppNavigator @Inject constructor(private val app: Application) : MainNavigator {

    override fun openSettings() {
        val intent = Intent(app,
            SettingsActivity::class.java)
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }

        app.startActivity(intent)
    }
}