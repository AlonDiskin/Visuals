package com.diskin.alon.videos.featuretesting

import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.presentation.R
import com.diskin.alon.visuals.videos.presentation.VideosAdapter.VideoViewHolder
import com.diskin.alon.visuals.videos.presentation.VideosFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.robolectric.Shadows
import java.util.concurrent.TimeUnit

/**
 * Step definitions for the 'User share videos' scenario.
 */
class ShareVideosSteps(
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
    private val testSelectedVideosUri: MutableList<Uri> = mutableListOf()
    private val deviceVideosSubject: Subject<List<MediaStoreVideo>> =
        BehaviorSubject.createDefault(testDeviceVideos)

    @Given("^User has public videos on device$")
    fun userHasPublicVideosOnDevice() {
        every{ mockedVideosProvider.getAll() } returns deviceVideosSubject
    }

    @And("^User opened the videos browser screen$")
    fun userOpenedTheVideosBrowserScreen() {
        // Launch pictures fragment
        scenario = FragmentScenario.launchInContainer(VideosFragment::class.java)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User selects \"([^\"]*)\" of them")
    fun userSelectsOfThem(videosNum: Int) {
        val expectedDisplayed = testDeviceVideos.sortedByDescending { it.added }
        // Select number videos(depending on test value)
        for (i in 0 until videosNum) {
            // long click to start multi selection(for first selected video),
            // or just click to select
            val action: ViewAction = if (i == 0) longClick() else click()

            onView(ViewMatchers.withId(R.id.videosList))
                .perform(scrollToPosition<VideoViewHolder>(i))
                .perform(actionOnItemAtPosition<VideoViewHolder>(i,action))

            testSelectedVideosUri.add(expectedDisplayed[i].uri)
        }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User share selected videos$")
    fun userShareSelectedVideos() {
        // Click on share menu item
        onView(withContentDescription(R.string.action_share_title))
            .perform(click())
    }

    @Then("^Videos browser should share the videos$")
    fun videosBrowserShouldShareTheVideos() {
        // Verify sharing intent is using the android sharing ui sheet
        intended(hasAction(Intent.ACTION_CHOOSER))
        intended(hasExtraWithKey(Intent.EXTRA_INTENT))

        // Verify selected videos uris are included in sharing intent, taking into account
        // that intent should be build differently for sharing single video, or multiple
        val intent = Intents.getIntents().first().extras.get(Intent.EXTRA_INTENT) as Intent
        val intentUris = if (testSelectedVideosUri.size > 1) {
            intent.extras.getParcelableArrayList(Intent.EXTRA_STREAM)!!
        } else {
            listOf(intent.extras.getParcelable<Uri>(Intent.EXTRA_STREAM)!!)
        }

        if (testSelectedVideosUri.size > 1) {
            assertThat(intent.action).isEqualTo(Intent.ACTION_SEND_MULTIPLE)
        } else{
            assertThat(intent.action).isEqualTo(Intent.ACTION_SEND)
        }

        assertThat(intent.type).isEqualTo("video/mp4")
        assertThat(intentUris.size).isEqualTo(testSelectedVideosUri.size)
        testSelectedVideosUri.forEach { assertThat(intentUris.contains(it)).isTrue() }
    }
}