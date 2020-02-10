package com.diskin.alon.visuals.settings.presentation

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.android.synthetic.main.activity_settings.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [SettingsActivity] unit test class
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class SettingsActivityTest {

    // System under test
    private lateinit var scenario: ActivityScenario<SettingsActivity>

    @Before
    fun setUp() {
        // Launch activity under test
        scenario = ActivityScenario.launch(SettingsActivity::class.java)
    }

    @Test
    fun showSettingsTitle_whenResumed() {
        // Given a resumed activity

        // Then activity should display the settings title in its toolbar
        scenario.onActivity {
            val expectedTitle = it.getString(R.string.settings_label)

            assertThat(it.toolbar.title).isEqualTo(expectedTitle)
        }
    }
}