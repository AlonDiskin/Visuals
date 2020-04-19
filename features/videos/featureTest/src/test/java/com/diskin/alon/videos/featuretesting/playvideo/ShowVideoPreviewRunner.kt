package com.diskin.alon.videos.featuretesting.playvideo

import com.diskin.alon.visuals.videos.featuretesting.VideosFeatureTestApp
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*

/**
 * Step definitions runner for the 'show video preview' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = VideosFeatureTestApp::class)
class ShowVideoPreviewRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("play_video.feature")
                .withTags("@show-video-preview")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }
    }

    @Test
    fun test() {
        // Launch scenario test
        start(ShowVideoPreviewSteps())
    }
}