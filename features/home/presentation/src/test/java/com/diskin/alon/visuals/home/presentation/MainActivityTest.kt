package com.diskin.alon.visuals.home.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjection
import io.mockk.*
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

/**
 * [MainActivity] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class MainActivityTest {

    // Test subject
    private lateinit var scenario: ActivityScenario<MainActivity>

    // Collaborators
    private val navigator: MainNavigator = mockk()
    private val themeManager: ThemeManager = mockk()

    @Before
    fun setUp() {
        // Mock out dagger injection
        mockkStatic(AndroidInjection::class)

        val activitySlot = slot<Activity>()

        every { AndroidInjection.inject(capture(activitySlot)) } answers {
            val mainActivity: MainActivity = activitySlot.captured as MainActivity
            mainActivity.mNavigator = navigator
            mainActivity.themeManager = themeManager
        }

        // Stub mocked collaborator behaviour
        every { navigator.getPicturesNavGraph() } returns R.navigation.pictures_test_nav_graph
        every { navigator.getVideosNavGraph() } returns R.navigation.videos_test_nav_graph
        every { navigator.getRecycleBinNavGraph() } returns R.navigation.recycle_bin_test_nav_graph
        every { themeManager.isDarkModeEnabled() } returns false

        // Currently(Feb 2020), robolectric has no capability to unit test or configure run time permissions, so
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
    fun openPicturesScreenComposite_whenUserNavigates() {
        // Given a resumed activity

        // When user click on 'pictures' menu item
        onView(withId(R.id.pictures))
            .perform(click())

        // Then nav controller should navigate to pictures destination in activity container
        scenario.onActivity {
            val actualDest = it.nav_host_container
                .findNavController().currentDestination!!.id

            assertThat(actualDest).isEqualTo(R.id.pictures_placeholder)
        }
    }

    @Test
    fun openVideosScreenComposite_whenUserNavigates() {
        // Given a resumed activity

        // When user click on 'videos' menu item
        onView(withId(R.id.videos))
            .perform(click())

        // Then nav controller should navigate to videos destination in activity container
        scenario.onActivity {
            val actualDest = it.nav_host_container
                .findNavController().currentDestination!!.id

            assertThat(actualDest).isEqualTo(R.id.videos_placeholder)
        }
    }

    @Test
    fun openRecycleBinScreenComposite_whenUserNavigates() {
        // Given a resumed activity

        // When user click on 'trash' menu item
        onView(withId(R.id.recycle_bin))
            .perform(click())

        // Then nav controller should navigate to recycle bin destination in activity container
        scenario.onActivity {
            val actualDest = it.nav_host_container
                .findNavController().currentDestination!!.id

            assertThat(actualDest).isEqualTo(R.id.recycle_bin_placeholder)
        }
    }

    @Test
    fun setDarkModeMenuItemAccordingToMode_WhenResumed() {
        // Given
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Then
        openActionBarOverflowOrOptionsMenu(context)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withId(R.id.checkbox))
            .check(matches(isNotChecked()))

        // When
        every { themeManager.isDarkModeEnabled() } returns true
        scenario.recreate()

        // Then
        openActionBarOverflowOrOptionsMenu(context)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withId(R.id.checkbox))
            .check(matches(isChecked()))
    }

    @Test
    fun setDarkModeMenuItemCheckState_AccordingToUserSelection() {
        // Given
        every { themeManager.setDarkMode(any()) } returns Unit

        // When
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withText(R.string.title_dark_mode))
            .perform(click())

        // Then
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withId(R.id.checkbox))
            .check(matches(isChecked()))

        // When
        every { themeManager.isDarkModeEnabled() } returns true
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withText(R.string.title_dark_mode))
            .perform(click())

        // Then
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withId(R.id.checkbox))
            .check(matches(isNotChecked()))
    }

    @Test
    fun setAppDarkMode_WhenUserSelectsMode() {
        // Given
        every { themeManager.setDarkMode(any()) } returns Unit

        // When
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withText(R.string.title_dark_mode))
            .perform(click())

        // Then
        verify { themeManager.setDarkMode(true) }

        // When
        every { themeManager.isDarkModeEnabled() } returns true
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withText(R.string.title_dark_mode))
            .perform(click())

        // Then
        verify { themeManager.setDarkMode(false) }
    }
}