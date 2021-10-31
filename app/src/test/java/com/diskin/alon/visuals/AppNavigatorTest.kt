package com.diskin.alon.visuals

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [AppNavigator] integration test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class AppNavigatorTest {

    // System under test
    private lateinit var appNavigator: AppNavigator

    @Before
    fun setUp() {
        // Initialize SUT
        appNavigator = AppNavigator(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun returnPicturesNavGraph_whenAskedFor() {
        assertThat(appNavigator.getPicturesNavGraph()).isEqualTo(R.navigation.pictures_nav_graph)
    }

    @Test
    fun returnVideosNavGraph_whenAskedFor() {
        assertThat(appNavigator.getVideosNavGraph()).isEqualTo(R.navigation.videos_nav_graph)
    }

    @Test
    fun returnRecycleBinNavGraph_whenAskedFor() {
        assertThat(appNavigator.getRecycleBinNavGraph()).isEqualTo(R.navigation.recycle_bin_nav_graph)
    }
}