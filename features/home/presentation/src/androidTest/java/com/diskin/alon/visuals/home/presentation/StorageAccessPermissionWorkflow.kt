package com.diskin.alon.visuals.home.presentation

import android.app.Activity
import android.widget.Button
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.google.common.truth.Truth
import dagger.android.AndroidInjection
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Storage access runtime permission test.
 */
@RunWith(AndroidJUnit4::class)
class StorageAccessPermissionWorkflow {

    // System under test
    private lateinit var scenario: ActivityScenario<MainActivity>

    // Mocked collaborators
    private val navigator: MainNavigator = mockk()

    // Global fixture properties
    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    @Before
    fun setUp() {
        // Stub mocked collaborators behaviour
        mockkStatic(AndroidInjection::class)

        val activitySlot = slot<Activity>()

        every { AndroidInjection.inject(capture(activitySlot)) } answers {
            val mainActivity: MainActivity = activitySlot.captured as MainActivity

            mainActivity.mNavigator = navigator
        }

        every { navigator.getPicturesNavGraph() } returns R.navigation.pictures_test_nav_graph
        every { navigator.getVideosNavGraph() } returns R.navigation.videos_test_nav_graph

        // Launch activity under test
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Wait for permission dialog to appear
        Thread.sleep(300)
    }

    @Test
    fun navToPicturesFragment_whenPermissionGranted() {
        // Given app to whom the user has not given storage access permission

        // And a resumed activity

        // Then activity should show run time permission dialog
        val allowButton = device.findObject(
            UiSelector()
                .className(Button::class.java)
                .textMatches("(?i:.*allow.*)*")
        )

        Truth.assertThat(allowButton.exists()).isTrue()

        // When user allow permission
        allowButton.click()

        // Then activity should navigate to pictures fragment
        verify { navigator.getPicturesNavGraph() }

        onView(withText(R.string.hello_pictures_fragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showToastMessage_whenPermissionDenied() {
        // Given app to whom the user has not given storage access permission

        // And a resumed activity

        // Then activity should show run time permission dialog
        val denyButton = device.findObject(
            UiSelector()
                .className(Button::class.java)
                .textMatches("(?i:.*deny.*)*")
        )

        Truth.assertThat(denyButton.exists()).isTrue()

        // When user deny permission
        denyButton.click()

        // Then activity should show a toast message
        onView(withText(R.string.permission_deny_message))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun notNavToFeatures_whenPermissionDenied() {
        // Given app to whom the user has not given storage access permission

        // And a resumed activity

        // Then activity should show run time permission dialog
        val denyButton = device.findObject(
            UiSelector()
                .className(Button::class.java)
                .textMatches("(?i:.*deny.*)*")
        )

        Truth.assertThat(denyButton.exists()).isTrue()

        // When user deny permission
        denyButton.click()

        // Then activity should not navigate to any app features fragments
        verify { navigator wasNot Called }
        onView(withText(R.string.hello_pictures_fragment))
            .check(doesNotExist())
    }
}