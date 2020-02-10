package com.diskin.alon.visuals.settings.presentation

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowAlertDialog

/**
 * [SettingsFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class SettingsFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<SettingsFragment>

    // Test fixture common properties
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)!!

    @Before
    fun setUp() {
        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(SettingsFragment::class.java,
            null, R.style.Theme_AppCompat_DayNight_DarkActionBar,null)
    }

    @Test
    fun showCurrentThemeAsPrefSummary_whenDisplayed() {
        // Given a resumed fragment

        // When user scrolls to theme pref
        onView(withClassName(equalTo(RecyclerView::class.java.name)))
            .perform(scrollTo<ViewHolder>(hasDescendant(withText(R.string.pref_theme_title))))
            .check(matches(isDisplayed()))

        // Then pref summary should display current theme entry
        val prefKey = context.getString(R.string.pref_theme_key)
        val prefValues = context.resources.getStringArray(R.array.pref_theme_list_values)
        val themePref = sharedPreferences.getString(prefKey,"")!!
        val index = prefValues.toList().indexOf(themePref)
        val expectedSummary = context.resources
            .getStringArray(R.array.pref_theme_list_entries)[index]

        onView(withText(expectedSummary))
            .check(matches(isDisplayed()))
    }

    @Test
    fun changeAppTheme_whenUserSelectsNewTheme() {
        // Given a resumed fragment

        // When user selects new theme
        val prefKey = context.getString(R.string.pref_theme_key)
        val prefValues = context.resources.getStringArray(R.array.pref_theme_list_values)
        val themeIndexToSelect = when(sharedPreferences.getString(prefKey,"")!!) {
            context.getString(R.string.pref_theme_day_value) ->
                prefValues.toList().indexOf(context.getString(R.string.pref_theme_night_value))

            else ->
                prefValues.toList().indexOf(context.getString(R.string.pref_theme_day_value))
        }

        onView(withClassName(equalTo(RecyclerView::class.java.name)))
            .perform(actionOnItem<ViewHolder>(
                hasDescendant(withText(R.string.pref_theme_title)), ViewActions.click()))

        val dialog =
            ShadowAlertDialog.getLatestDialog() as AlertDialog

        dialog.listView.performItemClick(
            dialog.listView.adapter.getView(themeIndexToSelect, null, null),
            themeIndexToSelect,
            dialog.listView.adapter.getItemId(themeIndexToSelect))

        // Then fragment should change night mode according to user selection
        val expectedNightMode = when(sharedPreferences.getString(prefKey,"")!!) {
            context.getString(R.string.pref_theme_day_value) -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_YES
        }

        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(expectedNightMode)
    }
}