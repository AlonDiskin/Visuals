package com.diskin.alon.visuals

import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given

/**
 * Step definitions for application usage workflow scenarios
 */
class AppWorkflowsSteps : GreenCoffeeSteps() {

    @Given("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // go to device home screen
        DeviceUtil.openDeviceHome()

        // launch app
        DeviceUtil.launchApp()

        // TODO create home screen for application, so app could be launched

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @And("^User navigates to settings screen$")
    fun userNavigatesToSettingsScreen() {
        // TODO create a navigation menu for settings screen from home screen

        // TODO create an app navigator that will navigate user to settings screen

        // TODO create empty settings screen

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}