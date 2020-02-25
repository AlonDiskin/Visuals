package com.diskin.alon.visuals

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
 * Step definitions runner for videos browser workflow scenario.
 */
@RunWith(Parameterized::class)
@LargeTest
class VideosFeaturesWorkflowRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/app_usage.feature")
                .withTags("@videos-browser-features")
                .scenarios()
        }
    }

    @Test
    fun test() {
        DeviceUtil.grantStorageAccessPermission()

        // Register idling resource for espresso
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        start(VideosFeaturesWorkflowSteps())
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        // Unregister espresso idling resource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}