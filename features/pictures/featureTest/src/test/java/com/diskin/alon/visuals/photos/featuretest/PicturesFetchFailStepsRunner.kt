package com.diskin.alon.visuals.photos.featuretest

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.diskin.alon.visuals.photos.featuretest.di.FeatureTestApp
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
 * Step definitions runner for the 'App fail to fetch device pictures' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = FeatureTestApp::class)
class PicturesFetchFailStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("list_pictures.feature")
                .withTags("@show-pictures-sad-path")
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
            .getApplicationContext<Context>() as FeatureTestApp)

        start(PicturesFetchFailSteps(featureTestApp.getMockedPicturesProvider()))
    }
}