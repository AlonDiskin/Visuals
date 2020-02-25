package com.diskin.alon.videos.featuretesting

import android.net.Uri
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.videos.featuretesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.videos.featuretesting.R
import com.diskin.alon.visuals.videos.presentation.ThumbnailLoader
import com.diskin.alon.visuals.videos.presentation.Video
import com.diskin.alon.visuals.videos.presentation.VideoDuration
import com.diskin.alon.visuals.videos.presentation.VideosFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verifyOrder
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.hamcrest.CoreMatchers.allOf
import org.robolectric.Shadows
import java.util.concurrent.TimeUnit

/**
 * Step definitions for the 'User public videos are shown' scenario.
 */
class UserVideosShownSteps(
    private val mockedVideosProvider: DeviceDataProvider<MediaStoreVideo>
) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<VideosFragment>
    private val testDeviceVideos = mutableListOf(
        MediaStoreVideo(
            Uri.parse("test uri 1"),
            10L,
            TimeUnit.MINUTES.toMillis(2) + TimeUnit.SECONDS.toMillis(10)
        ),
        MediaStoreVideo(
            Uri.parse("test uri 2"),
            900L,
            TimeUnit.SECONDS.toMillis(40)
        ),
        MediaStoreVideo(
            Uri.parse("test uri 3"),
            30L,
            TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(10)
        ),
        MediaStoreVideo(
            Uri.parse("test uri 4"),
            140L,
            TimeUnit.SECONDS.toMillis(37)
        )
    )
    private val expectedVideos = listOf(
        Video(testDeviceVideos[1].uri, VideoDuration(40,0)),
        Video(testDeviceVideos[3].uri, VideoDuration(37,0)),
        Video(testDeviceVideos[2].uri, VideoDuration(10,1)),
        Video(testDeviceVideos[0].uri, VideoDuration(10,2))
    )
    private val deviceVideosSubject: Subject<List<MediaStoreVideo>> =
        BehaviorSubject.createDefault(testDeviceVideos)

    @Given("^User has public videos on device$")
    fun givenUserHasPublicVideosOnDevice() {
        every{ mockedVideosProvider.getAll() } returns deviceVideosSubject
    }

    @When("^User opens videos browser screen$")
    fun userOPensVideosBrowserScreen() {
        // Mock Thumbnail loader before launching
        mockkObject(ThumbnailLoader)

        // Launch pictures fragment
        scenario = FragmentScenario.launchInContainer(VideosFragment::class.java)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^All videos are shown sorted by date in descending order$")
    fun allVideosAreShownSortedByDateInDescendingOrder() {
        // Verify all test videos are displayed in sorted descending order
        verifyOrder {
            expectedVideos.forEach {
                ThumbnailLoader.load(any(),it.uri)
            }
        }

        expectedVideos.forEachIndexed { index, video ->
            onView(withRecyclerView(R.id.videosList).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.videoDuration),
                                withText(video.duration.getFormattedDuration()),
                                isDisplayed())
                        )
                    )
                )
        }
    }

    @When("^User removes a video from device$")
    fun userRemovesVideoFromDevice() {
        // Remove a video fro test data set
        testDeviceVideos.removeAt(0)
        deviceVideosSubject.onNext(testDeviceVideos)
    }

    @Then("^Videos browser screen should update shown videos accordingly$")
    fun videosBrowserScreenShouldUpdateShownVideosAccordingly() {
        // Verify displayed pictures are updated
        expectedVideos.forEachIndexed { index, video ->
            onView(withRecyclerView(R.id.videosList).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.videoDuration),
                                withText(video.duration.getFormattedDuration()),
                                isDisplayed())
                        )
                    )
                )
        }
    }
}