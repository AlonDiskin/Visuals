package com.diskin.alon.visuals.settings

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.util.DeviceUtil
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When

/**
 * Step definitions for application usage workflow scenarios.
 */
class SettingsFeatureWorkflowsSteps : GreenCoffeeSteps() {

    @Given("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    @Then("^App visual theme should be set as day theme$")
    fun appVisualThemeShouldBeSetAsDayTheme() {
        // Verify app is day mode
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_NO) // defaults to day

        // Verify shared preferences value for theme is day theme value
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val themeKey = context.getString(R.string.pref_dark_mode_key)
        val themeDefault = context.getString(R.string.pref_dark_mode_default_value)
        val themePref = sharedPreferences.getString(themeKey,themeDefault)!!

        assertThat(themePref).isEqualTo(themeDefault)

        // Verify settings screen showing current theme as day theme
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withId(R.id.checkbox))
            .check(matches(isNotChecked()))
    }

    @When("^User selects the night theme$")
    fun userSelectsTheNightTheme() {
        // Select night theme
        onView(withText(R.string.title_dark_mode))
            .perform(click())
    }

    @Then("^App theme should be changed to night$")
    fun appThemeShouldBeChangedToNight() {
        // Verify app compat delegate is changed to night mode
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)

        // Verify theme preserved when user exists and returns to app

        // Exit app
        DeviceUtil.pressBack()

        // Return to app
        DeviceUtil.launchApp()

        // Verify theme set to current pref selection
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)

        // Return day theme
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.title_dark_mode))
            .perform(click())
    }
}