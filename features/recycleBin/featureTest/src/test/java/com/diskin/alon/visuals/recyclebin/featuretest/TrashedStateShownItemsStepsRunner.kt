package com.diskin.alon.visuals.recyclebin.featuretest

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*

/**
 * Step definitions runner for the 'Trashed items are displayed to user' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TestApp::class)
class TrashedStateShownItemsStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("list_trashed_items.feature")
                .withTags("@trashed-state-shown")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }
    }

    @Test
    fun test() {
        val testApp = ApplicationProvider.getApplicationContext<Context>() as TestApp

        start(
            TrashedStateShownItemsSteps(
                testApp.getTestTrashedItemsDao(),
                testApp.getDeviceMediaProvider()
            )
        )
    }
}