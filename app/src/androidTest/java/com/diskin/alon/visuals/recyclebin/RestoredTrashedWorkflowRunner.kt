package com.diskin.alon.visuals.recyclebin

import androidx.test.espresso.IdlingRegistry
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
 * Step definitions runner for the 'User restores trashed items' scenario.
 */
@RunWith(Parameterized::class)
@LargeTest
class RestoredTrashedWorkflowRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    private lateinit var testSteps: RestoreTrashedWorkflowSteps

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/recycle_bin_workflows.feature")
                .withTags("@restore-trashed")
                .scenarios()
        }
    }

    @Test
    fun test() {
        DeviceUtil.grantStorageAccessPermission()

        // Register idling resource for espresso
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // Init RxIdler
        RxJavaPlugins.setInitComputationSchedulerHandler(
            Rx2Idler.create("RxJava 2.x Computation Scheduler"))
        RxJavaPlugins.setInitIoSchedulerHandler(
            Rx2Idler.create("RxJava 2.x IO Scheduler"))

        // Run scenario test steps
        testSteps = RestoreTrashedWorkflowSteps()
        start(testSteps)
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        // Purge test data from test device
        testSteps.deleteAllTestDataFromDevice()

        // Unregister espresso idling resource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}