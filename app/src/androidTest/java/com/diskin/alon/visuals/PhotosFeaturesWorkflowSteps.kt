package com.diskin.alon.visuals

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers

/**
 * Step definitions for photos browser workflow scenarios.
 */
class PhotosFeaturesWorkflowSteps : GreenCoffeeSteps() {

    @Given("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    @When("^User navigates to photos browser screen$")
    fun userNavigatesToPhotosBrowserScreen() {
        // Navigate to photos browser screen
        onView(
            CoreMatchers.allOf(
                withText(R.string.nav_photos),
                isDisplayed()
            )
        )
            .perform(ViewActions.click())

        // Verify browser screen displayed
        onView(withText(R.string.hello_photos_fragment))
            .check(matches(isDisplayed()))
    }
}