package com.diskin.alon.visuals.settings.presentation

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
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

    @Test
    fun showUpNavigation_whenResumed() {
        // Given a resumed activity

        // Then activity should show an up navigation action button
        onView(withContentDescription("Navigate up"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun finishActivity_whenUpNavigationClicked() {
        // Given a resumed activity

        // When performs up navigation
        onView(withContentDescription("Navigate up")).perform(click())

        // Then activity should finish
        scenario.onActivity {
            assertThat(it.isFinishing).isTrue()
        }
    }
}