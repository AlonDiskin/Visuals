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
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

/**
 * Step definitions runner for videos browser workflow scenario.
 */
@RunWith(Parameterized::class)
@LargeTest
class VideosBrowserWorkflowRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    private lateinit var steps: VideosBrowserWorkflowSteps

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/videos_workflows.feature")
                .withTags("@videos-browser")
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

        // Init RxIdler
        RxJavaPlugins.setInitComputationSchedulerHandler(
            Rx2Idler.create("RxJava 2.x Computation Scheduler"))
        RxJavaPlugins.setInitIoSchedulerHandler(
            Rx2Idler.create("RxJava 2.x IO Scheduler"))

        steps = VideosBrowserWorkflowSteps()

        start(steps)
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        // Unregister espresso idling resource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)

        // Release platform intents validation api
        Intents.release()

        // Purge test device from test data that was installed\modified on him
        steps.deleteTestDataFromDevice()
    }
}