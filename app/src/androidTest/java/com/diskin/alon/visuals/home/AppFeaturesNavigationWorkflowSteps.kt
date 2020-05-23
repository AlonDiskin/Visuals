package com.diskin.alon.visuals.home

import android.app.ActivityManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.home.presentation.MainActivity
import com.diskin.alon.visuals.settings.presentation.SettingsActivity
import com.diskin.alon.visuals.util.DeviceUtil
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.allOf

/**
 * Step definitions for 'User navigates to app features workflow' scenario.
 */
class AppFeaturesNavigationWorkflowSteps : GreenCoffeeSteps() {

    @Given("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    @And("^App home screen is displayed$")
    fun appHomeScreenIsDisplayed() {
        // Verify home screen displayed
        val am = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        val foregroundActivityName =  tasks.first().topActivity.className
        val expectedForeGroundActivityName = MainActivity::class.java.name

        assertThat(foregroundActivityName).isEqualTo(expectedForeGroundActivityName)
    }

    @When("^User navigates to settings$")
    fun userNavigatesToSettings() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.action_settings))
            .perform(click())
    }

    @Then("^Settings should be displayed in own screen$")
    fun settingsShouldBeDisplayedInOwnScreen() {
        // Verify settings screen displayed
        val am = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        val foregroundActivityName =  tasks.first().topActivity.className
        val expectedForeGroundActivityName = SettingsActivity::class.java.name

        assertThat(foregroundActivityName).isEqualTo(expectedForeGroundActivityName)
    }

    @When("^User navigates to videos browser$")
    fun userNavigatesToVideosBrowser() {
        DeviceUtil.pressBack()

        onView(withId(R.id.videos))
            .perform(click())
    }

    @Then("^Videos browser ui should show as composite in home screen$")
    fun videosBrowserUiShouldShowAsCompositeInHomeScreen() {
        onView(withId(R.id.fragment_videos_root))
            .check(matches(isDisplayed()))
    }

    @When("^User navigates to pictures browser$")
    fun userNavigatesToPicturesBrowser() {
        onView(withId(R.id.pictures))
            .perform(click())
    }

    @Then("^Pictures browser ui should show as composite in home screen$")
    fun picturesBrowserUiShouldShowAsCompositeInHomeScreen() {
        onView(withId(R.id.fragment_pictures_root))
            .check(matches(isDisplayed()))
    }

    @When("^User navigates to recycle bin$")
    fun userNavigatesToRecycleBin() {
        onView(withId(R.id.recycle_bin))
            .perform(click())
    }

    @Then("^Recycle bin ui should show as composite in home screen$")
    fun recycleBinUiShouldShowAsCompositeInHomeScreen() {
        onView(withId(R.id.recycle_bin_browser_fragment_root))
            .check(matches(isDisplayed()))
    }
}