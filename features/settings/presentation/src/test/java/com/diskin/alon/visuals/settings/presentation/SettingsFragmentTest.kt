package com.diskin.alon.visuals.settings.presentation

import android.content.Context
import android.preference.PreferenceManager
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [SettingsFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class SettingsFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<SettingsFragment>

    // Test data
    private val context: Context = ApplicationProvider.getApplicationContext()

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
        onView(withClassName(CoreMatchers.equalTo(RecyclerView::class.java.name)))
            .perform(scrollTo<ViewHolder>(hasDescendant(withText(R.string.pref_theme_title))))
            .check(matches(isDisplayed()))

        // Then pref summary should display current theme entry
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)!!
        val prefKey = context.getString(R.string.pref_theme_key)
        val prefValues = context.resources.getStringArray(R.array.pref_theme_list_values)
        val themePref = sharedPreferences.getString(prefKey,"")!!
        val index = prefValues.toList().indexOf(themePref)
        val expectedSummary = context.resources
            .getStringArray(R.array.pref_theme_list_entries)[index]

        onView(withText(expectedSummary))
            .check(matches(isDisplayed()))
    }
}