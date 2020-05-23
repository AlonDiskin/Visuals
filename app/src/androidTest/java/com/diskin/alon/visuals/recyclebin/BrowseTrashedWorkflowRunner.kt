package com.diskin.alon.visuals.recyclebin

import androidx.test.espresso.IdlingRegistry
import androidx.test.filters.LargeTest
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource
import com.diskin.alon.visuals.util.DeviceUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.Scenario
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

/**
 * Step definitions runner for the 'Trashed items are listed' scenario.
 */
@RunWith(Parameterized::class)
@LargeTest
class BrowseTrashedWorkflowRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    private lateinit var testSteps: BrowseTrashedWorkflowSteps

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/recycle_bin_workflows.feature")
                .withTags("@browse-trashed-items")
                .scenarios()
        }
    }

    @Test
    fun test() {
        DeviceUtil.grantStorageAccessPermission()

        // Register idling resource for espresso
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        testSteps = BrowseTrashedWorkflowSteps()

        start(testSteps)
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        // Purge test data from test device
        testSteps.deleteAllTestDataFromDevice()

        // Unregister espresso idling resource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}