package com.diskin.alon.visuals

import androidx.test.filters.LargeTest
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Step definitions runner for application usage workflow scenarios
 */
@RunWith(Parameterized::class)
@LargeTest
class AppWorkflowRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/app_usage.feature")
                .scenarios()
        }
    }

    @Test
    fun test() {
        start(AppWorkflowsSteps())
    }
}