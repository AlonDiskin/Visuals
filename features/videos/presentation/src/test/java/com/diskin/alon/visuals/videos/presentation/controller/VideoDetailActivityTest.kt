package com.diskin.alon.visuals.videos.presentation.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.videos.presentation.R
import com.diskin.alon.visuals.viedos.presentation.StubVideoPreviewFragment
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjection
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [VideoDetailActivity] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class VideoDetailActivityTest {

    // System under test
    private lateinit var scenario: ActivityScenario<VideoDetailActivity>

    // Mocked collaborators
    private val fragmentsFactory: VideoPlayerFragmentsFactory = mockk()

    // Stub data
    private val videoUri: Uri = mockk()

    @Before
    fun setUp() {
        // Mock out dagger injection
        mockkStatic(AndroidInjection::class)

        val activitySlot = slot<Activity>()

        // Stub mocked dagger behaviour
        every { AndroidInjection.inject(capture(activitySlot)) } answers {
            val videoPlayerActivity = activitySlot.captured as VideoDetailActivity
            videoPlayerActivity.fragmentsFactory = fragmentsFactory
        }

        // Stub mocked fragment factory behaviour
        every { fragmentsFactory.createVideoPreviewFragment(videoUri) } returns StubVideoPreviewFragment()

        // Launch activity under test
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val intent = Intent(context, VideoDetailActivity::class.java).apply {
            putExtra(context.getString(R.string.extra_vid_uri),videoUri)
        }

        scenario = ActivityScenario.launch(intent)
    }

    @Test
    fun showEmptyAppBarTitle_whenResumed() {
        // Given a resumed activity

        // Then activity app bar title should be empty
        scenario.onActivity {
            val toolbar = it.findViewById<Toolbar>(R.id.toolbar)!!

            assertThat(toolbar.title).isEqualTo("")
        }
    }

    @Test
    fun displayVideoPreview_whenOpened() {
        // Given a resumed activity

        // Then activity should create a video preview fragment from factory with passed uri
        verify { fragmentsFactory.createVideoPreviewFragment(videoUri) }

        // And display it in layout
        onView(withText(R.string.stub_video_frag_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun provideAppBarUpNavigation_whenResumed() {
        // Given a resumed activity

        // Then activity should provide up navigation in its app bar menu
        onView(withContentDescription(R.string.abc_action_bar_up_description))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showLayoutAsMediaDetailTheme_whenResumed() {
        // Given a resumed activity

        // Then activity theme layout should be media detail theme
        scenario.onActivity {
            val context: Context = ApplicationProvider.getApplicationContext()
            val packageManager: PackageManager = context.packageManager
            val activityInfo: ActivityInfo =
                packageManager.getActivityInfo(it.componentName, PackageManager.GET_META_DATA)
            val actualThemeResId = activityInfo.theme

            assertThat(actualThemeResId).isEqualTo(R.style.AppTheme_NoActionBar_MediaDetailActivityTheme)
        }
    }
}