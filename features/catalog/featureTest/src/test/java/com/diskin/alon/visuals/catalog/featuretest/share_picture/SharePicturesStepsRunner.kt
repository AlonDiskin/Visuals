package com.diskin.alon.visuals.catalog.featuretest.share_picture

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import com.diskin.alon.visuals.catalog.featuretest.TestApp
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.Scenario
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*

/**
 * Step definitions runner for the 'User share pictures' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TestApp::class)
class SharePicturesStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("share_pictures.feature")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }
    }

    @Test
    fun test() {
        // Init intents validation api
        Intents.init()

        val featureTestApp = (ApplicationProvider
            .getApplicationContext<Context>() as TestApp)

        // Launch scenario test
        start(SharePicturesSteps(featureTestApp.getMockedPicturesProvider()))
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        // Release intents validation api
        Intents.release()
    }
}