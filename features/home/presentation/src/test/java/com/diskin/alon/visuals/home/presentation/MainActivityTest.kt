package com.diskin.alon.visuals.home.presentation

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.home.presentation.di.TestApp
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.verify
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import javax.inject.Inject


/**
 * [MainActivity] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(application = TestApp::class)
class MainActivityTest {

    // System under test
    private lateinit var scenario: ActivityScenario<MainActivity>

    // Mocked SUT collaborators
    @Inject
    lateinit var navigator: MainNavigator

    @Before
    fun setUp() {
        // Launch main activity
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun showAppNameInToolbar_whenResumed() {
        // Given a resumed activity

        // Then app name should be displayed is activity toolbar title
        scenario.onActivity {
            val expectedTitle = it.getString(R.string.app_name)

            assertThat(it.toolbar.title).isEqualTo(expectedTitle)
        }
    }

    @Test
    fun navToSettingsScreen_whenUserNavToSettings() {
        // Given a resumed activity

        // When user open options menu
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        // And clicks on 'settings' item
        onView(withText(R.string.action_settings))
            .perform(click())

        // Then navigation helper should navigate to settings screen
        verify(navigator).openSettings()
    }
}