package com.diskin.alon.videos.featuretesting.listvideos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.diskin.alon.videos.featuretesting.listvideos.UserVideosShownSteps
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
 * Step definitions runner for the 'User public videos are shown' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = VideosFeatureTestApp::class)
class UserVideosShownStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("list_videos.feature")
                .withTags("@show-videos-happy-path")
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
            .getApplicationContext<Context>() as VideosFeatureTestApp)

        start(
            UserVideosShownSteps(
                featureTestApp.getMockedVideosProvider()
            )
        )
    }
}