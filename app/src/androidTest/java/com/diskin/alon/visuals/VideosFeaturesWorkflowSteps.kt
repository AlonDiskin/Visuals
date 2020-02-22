package com.diskin.alon.visuals

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.util.DeviceUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import org.hamcrest.CoreMatchers.allOf

/**
 * Step definitions for videos browser workflow scenario.
 */
class VideosFeaturesWorkflowSteps : GreenCoffeeSteps() {

    @Given("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    @And("^User navigates to videos browser screen$")
    fun userNavigatesToVideosBrowserScreen() {
        // Navigate to videos screen from home screen
        onView(allOf(withText(R.string.nav_pictures),isDisplayed()))
            .perform(ViewActions.click())

        // Verify browser screen displayed
        onView(withId(R.id.fragment_pictures_root))
            .check(ViewAssertions.matches(isDisplayed()))
    }
}