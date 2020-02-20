package com.diskin.alon.visuals.home.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.home.presentation.di.TestApp
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.android.synthetic.main.activity_main.*
import org.hamcrest.CoreMatchers.allOf
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
        // Setup stub data

        whenever(navigator.getPicturesNavGraph()).doReturn(R.navigation.photos_test_nav_graph)

        // Currently(Feb 2020), robolectric has no capability to unit test run time permissions, so
        // we just going to stub a granted permission, while user run time permission flow will be
        // tested separately.
        mockkStatic(ContextCompat::class)
        every { ContextCompat.checkSelfPermission(
            any(),
            Manifest.permission.READ_EXTERNAL_STORAGE) } returns PackageManager.PERMISSION_GRANTED

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
    fun navToSettingsScreen_whenClickingOnSettingsNavMenu() {
        // Given a resumed activity

        // When user open options menu
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        // And clicks on 'settings' menu item
        onView(withText(R.string.action_settings))
            .perform(click())

        // Then navigation helper should navigate to settings screen
        verify(navigator).openSettings()
    }

    @Test
    fun navToPhotosBrowserScreen_whenClickingOnPhotosNavMenu() {
        // Given a resumed activity
        //verify(this.navigator).getPicturesNavGraph()

        // When user click on 'photos' menu item
        onView(allOf(withText(R.string.nav_pictures),isDisplayed()))
            .perform(click())

        // Then nav controller should navigate to photos browser destination
        onView(withText(R.string.hello_blank_fragment))
            .check(matches(isDisplayed()))

    }
}