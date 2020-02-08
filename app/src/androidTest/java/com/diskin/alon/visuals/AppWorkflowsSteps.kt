package com.diskin.alon.visuals

import android.app.ActivityManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.diskin.alon.visuals.settings.presentation.SettingsActivity
import com.google.common.truth.Truth.assertThat
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
    }

    @And("^User navigates to settings screen$")
    fun userNavigatesToSettingsScreen() {
        // navigate to settings screen
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Espresso.onView(ViewMatchers.withText(R.string.action_settings))
            .perform(ViewActions.click())

        // verify settings screen displayed
        val am = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        val foregroundActivityName =  tasks.first().topActivity.className
        val expectedForeGroundActivityName = SettingsActivity::class.java.name

        assertThat(foregroundActivityName).isEqualTo(expectedForeGroundActivityName)
    }
}