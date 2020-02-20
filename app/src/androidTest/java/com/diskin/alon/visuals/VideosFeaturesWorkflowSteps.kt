package com.diskin.alon.visuals

import com.diskin.alon.visuals.util.DeviceUtil
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given

/**
 * Step definitions for videos browser workflow scenario.
 */
class VideosFeaturesWorkflowSteps {

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
        TODO("Not implemented yet!")
    }
}