package com.diskin.alon.visuals.videos.presentation

import android.net.Uri
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.videos.presentation.VideosAdapter.VideoViewHolder
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

/**
 * [VideosFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class VideosFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<VideosFragment>

    // Mocked collaborator
    private val viewModel = mockk<VideosViewModel>()

    //Collaborator stub data
    private val videosLiveData = MutableLiveData<List<Video>>()
    private val updateFailEvent = SingleLiveEvent<String>()

    @Before
    fun setUp() {
        // Mock out dagger injection
        mockkStatic(AndroidSupportInjection::class)

        val fragmentSlot = slot<Fragment>()

        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val videosFragment = fragmentSlot.captured as VideosFragment
            videosFragment.viewModel = viewModel
        }

        // Stub mocked collaborator behaviour
        every{ viewModel.videos } returns videosLiveData
        every { viewModel.videosUpdateFail } returns updateFailEvent

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(VideosFragment::class.java)
    }

    @Test
    fun showVideos_whenVideosUpdated() {
        // Test case fixture
        mockkObject(ThumbnailLoader)

        // Given a resumed fragment

        // When view model update videos
        val testVideos = arrayListOf(
            Video(Uri.parse("uri 1"), VideoDuration(10,2)),
            Video(Uri.parse("uri 2"),VideoDuration(40,0)),
            Video(Uri.parse("uri 3"),VideoDuration(5,1))
        )

        videosLiveData.value = testVideos
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show updated videos in layout
        testVideos.forEachIndexed { index, video ->
            verify { ThumbnailLoader.load(any(),video.uri) }

            onView(withId(R.id.videosList))
                .perform(RecyclerViewActions.scrollToPosition<VideoViewHolder>(index))

            onView(allOf(
                withId(R.id.videoDuration),
                withText(video.duration.getFormattedDuration())
            ))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun showErrorMessage_whenVideosUpdateFail() {
        // Given a resumed fragment

        // When view model raise a videos update failure event
        val testFailMessage = "fail message"

        updateFailEvent.value = testFailMessage
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show event error message
        assertThat(ShadowToast.getTextOfLatestToast().toString()).isEqualTo(testFailMessage)
    }
}