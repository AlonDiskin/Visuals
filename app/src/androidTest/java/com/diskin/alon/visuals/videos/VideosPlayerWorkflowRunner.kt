package com.diskin.alon.visuals.videos

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.intent.Intents
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
 * Step definitions runner for videos viewer workflow scenario.
 */
@RunWith(Parameterized::class)
@LargeTest
class VideosPlayerWorkflowRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    lateinit var steps: VideosPlayerWorkflowSteps

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/videos_workflows.feature")
                .withTags("@videos-player")
                .scenarios()
        }
    }

    @Test
    fun test() {
        DeviceUtil.grantStorageAccessPermission()

        // Register idling resource for espresso
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // Init platform intents validation api
        Intents.init()

        steps = VideosPlayerWorkflowSteps()
        start(steps)
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        // Unregister espresso idling resource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)

        // Release platform intents validation api
        Intents.release()

        // Delete test videos from test device
        steps.deleteTestVideosFromDevice()
    }
}