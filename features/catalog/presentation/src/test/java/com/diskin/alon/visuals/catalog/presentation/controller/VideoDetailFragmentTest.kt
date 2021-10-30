package com.diskin.alon.visuals.catalog.presentation.controller

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.catalog.presentation.model.VideoDetail
import com.diskin.alon.visuals.catalog.presentation.model.VideoInfoError
import com.diskin.alon.visuals.catalog.presentation.viewmodel.VideoDetailViewModel
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

/**
 * [VideoDetailFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class VideoDetailFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<VideoDetailFragment>

    // Mocked collaborator
    private val viewModel = mockk<VideoDetailViewModel>()

    // Stub data
    private val videoDetailData = MutableLiveData<VideoDetail>()
    private val videoErrorEvent = SingleLiveEvent<VideoInfoError>()

    @Before
    fun setUp() {
        // Mock dagger
        mockkStatic(AndroidSupportInjection::class)
        val fragmentSlot = slot<VideoDetailFragment>()

        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val videosFragment = fragmentSlot.captured
            videosFragment.viewModel = viewModel
        }

        // Stub mocked collaborator
        every { viewModel.videoDetail } returns videoDetailData
        every { viewModel.videoInfoError } returns videoErrorEvent

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(VideoDetailFragment::class.java)
    }

    @Test
    fun showVideoInfo_whenResumed() {
        // Given a resumed activity

        // When fragments view model update its video detail state
        val testVideoDetail = VideoDetail(
            "114.67",
            "Jul 2 2017 14:55",
            "1:47",
            "path",
            "my_vid.mp4",
            "1960*1200"
        )

        videoDetailData.value = testVideoDetail

        // Then fragment should display video detail in layout
        onView(withId(R.id.sizeDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(testVideoDetail.size)
                )
            ))

        onView(withId(R.id.dateDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(testVideoDetail.added)
                )
            ))

        onView(withId(R.id.pathDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(testVideoDetail.path)
                )
            ))

        onView(withId(R.id.titleDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(testVideoDetail.title)
                )
            ))

        onView(withId(R.id.resolutionDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(testVideoDetail.resolution)
                )
            ))

        onView(withId(R.id.durationDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(testVideoDetail.duration)
                )
            ))
    }

    @Test
    fun showErrorMessage_whenVideoInfoNotAvailable() {
        // Given a resumed activity

        // When fragments view model raise an error event for video info
        val testError = VideoInfoError("error message")
        videoErrorEvent.value = testError

        // Then fragment should show a toast message with event message
        val actualToastMessage = ShadowToast.getTextOfLatestToast().toString()
        assertThat(actualToastMessage).isEqualTo(testError.message)
    }
}