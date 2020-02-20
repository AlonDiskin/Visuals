package com.diskin.alon.visuals

import android.widget.Button
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.diskin.alon.visuals.home.presentation.MainActivity
import com.diskin.alon.visuals.util.DeviceUtil
import com.diskin.alon.visuals.util.ToastMatcher
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Storage access runtime permission test.
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class StorageAccessPermissionWorkflow {

    private lateinit var scenario: ActivityScenario<MainActivity>
    lateinit var device: UiDevice

    @Before
    fun setUp() {
        device = DeviceUtil.getDevice()
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Wait for dialog window to appear
        Thread.sleep(500)
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
    fun notNavToPicturesFragment_whenPermissionDenied() {
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

        // Then activity should not open pictures fragment
        onView(withId(R.id.fragment_pictures_root))
            .check(doesNotExist())
    }

    @Test
    fun navToPicturesFragment_whenPermissionGranted() {
        // Given app to whom the user has not given storage access permission

        // And a resumed activity

        // Then activity should show run time permission dialog
        //Thread.sleep(100)
        val allowButton = device.findObject(
            UiSelector()
                .className(Button::class.java)
                .textMatches("(?i:.*allow.*)*")
        )

        Truth.assertThat(allowButton.exists()).isTrue()

        // When user allow permission
        allowButton.click()

        // Then activity should navigate to pictures fragment
        onView(withId(R.id.fragment_pictures_root))
            .check(matches(isDisplayed()))
    }
}