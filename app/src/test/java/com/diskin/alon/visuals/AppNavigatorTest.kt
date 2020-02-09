package com.diskin.alon.visuals

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diskin.alon.visuals.settings.presentation.SettingsActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [AppNavigator] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class AppNavigatorTest {

    // System under test
    private lateinit var appNavigator: AppNavigator

    @Before
    fun setUp() {
        // Initialize SUT
        appNavigator = AppNavigator(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun startSettingsActivity_whenOpeningSettings() {
        Intents.init()

        // Given an initialized navigator

        // When client opens settings screen
        appNavigator.openSettings()

        // Then navigator should start the settings activity
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
        Intents.release()
    }
}