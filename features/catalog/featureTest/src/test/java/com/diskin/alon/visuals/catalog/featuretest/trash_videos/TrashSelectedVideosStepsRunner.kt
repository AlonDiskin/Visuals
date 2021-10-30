package com.diskin.alon.visuals.catalog.featuretest.trash_videos

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.diskin.alon.visuals.catalog.featuretest.TestVideosApp
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*

/**
 * Step definitions runner for the 'User trash selected videos' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TestVideosApp::class)
class TrashSelectedVideosStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("trash_videos.feature")
                .withTags("@trash-selected-videos")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    @Test
    fun test() {
        val featureTestApp = (ApplicationProvider
            .getApplicationContext<Context>() as TestVideosApp)

        start(
            TrashSelectedVideosSteps(
                featureTestApp.getMockedVideosProvider(),
                featureTestApp.getTestTrashedDao()
            )
        )
    }
}