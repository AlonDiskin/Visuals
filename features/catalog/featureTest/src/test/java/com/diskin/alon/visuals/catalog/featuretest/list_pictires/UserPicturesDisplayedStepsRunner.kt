package com.diskin.alon.visuals.catalog.featuretest.list_pictires

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.diskin.alon.visuals.catalog.featuretest.TestApp
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.ArrayList

/**
 * Step definitions runner for the 'User device photos displayed' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TestApp::class)
class UserPicturesDisplayedStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("list_pictures.feature")
                .withTags("@show-pictures-happy-path")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }
    }

    @Test
    fun test() {
        val featureTestApp = (ApplicationProvider
            .getApplicationContext<Context>() as TestApp)

        start(UserPicturesDisplayedSteps(featureTestApp.getMockedPicturesProvider()))
    }
}