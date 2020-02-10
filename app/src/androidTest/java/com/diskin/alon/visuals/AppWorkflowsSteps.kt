package com.diskin.alon.visuals

import android.app.ActivityManager
import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.home.presentation.MainActivity
import com.diskin.alon.visuals.settings.presentation.SettingsActivity
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.equalTo


/**
 * Step definitions for application usage workflow scenarios
 */
class AppWorkflowsSteps : GreenCoffeeSteps() {

    @Given("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    @When("^User navigates to default values settings screen$")
    fun userNavigatesToDefaultValuesSettingsScreen() {

        // Navigate to settings screen
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.action_settings))
            .perform(click())

        // Verify settings screen displayed
        val am = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        val foregroundActivityName =  tasks.first().topActivity.className
        val expectedForeGroundActivityName = SettingsActivity::class.java.name

        assertThat(foregroundActivityName).isEqualTo(expectedForeGroundActivityName)
    }

    @Then("^App visual theme should be set as day theme$")
    fun appVisualThemeShouldBeSetAsDayTheme() {
        // Verify app is day mode
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_NO) // defaults to day

        // Verify shared preferences value for theme is day theme value
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val themeKey = context.getString(R.string.pref_theme_key)
        val themePref = sharedPreferences.getString(themeKey,"")!!
        val expectedThemePref = context.getString(R.string.pref_theme_day_value)

        assertThat(themePref).isEqualTo(expectedThemePref)

        // Verify settings screen showing current theme as day theme

        // Check pref showing day theme entry value as its summary
        val prefValues = context.resources.getStringArray(R.array.pref_theme_list_values)
        val index = prefValues.toList().indexOf(themePref)
        val expectedSummary = context.resources
            .getStringArray(R.array.pref_theme_list_entries)[index]

        onView(withClassName(equalTo(RecyclerView::class.java.name)))
            .perform(scrollTo<ViewHolder>(
                    hasDescendant(withText(R.string.pref_theme_title))))
            .check(matches(isDisplayed()))

        onView(withText(expectedSummary))
            .check(matches(isDisplayed()))

        // Check pref selection dialog checks day theme
        onView(withClassName(equalTo(RecyclerView::class.java.name)))
            .perform(actionOnItem<ViewHolder>(
                hasDescendant(withText(R.string.pref_theme_title)),click()))

        onView(withText(R.string.pref_theme_day_value))
            .inRoot(isDialog())
            .check(matches(isChecked()))
    }

    @When("^User selects the night theme$")
    fun userSelectsTheNightTheme() {
        // Select night theme
        onView(withText(R.string.pref_theme_night_value))
            .inRoot(isDialog())
            .perform(click())
    }

    @Then("^App theme should be changed to night$")
    fun appThemeShouldBeChangedToNight() {
        // Verify app compat delegate is changed to night mode
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)

        // Verify theme preserved when user exists and returns to app

        // Return to home screen
        DeviceUtil.pressBack()

        // Verify Home screen displayed
        val am = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        val foregroundActivityName =  tasks.first().topActivity.className
        val expectedForeGroundActivityName = MainActivity::class.java.name

        assertThat(foregroundActivityName).isEqualTo(expectedForeGroundActivityName)

        // Exit app
        DeviceUtil.pressBack()

        // Return to app
        DeviceUtil.launchApp()

        // Verify theme set to current pref selection
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)
    }
}