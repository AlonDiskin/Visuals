package com.diskin.alon.videos.featuretesting.playvideo

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Looper
import android.widget.VideoView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.visuals.videos.presentation.R
import com.diskin.alon.visuals.videos.presentation.controller.VideoPlayerActivity
import com.diskin.alon.visuals.videos.presentation.controller.VideoPreviewFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowVideoView

/**
 * Step definitions of the 'show video preview' scenario.
 */
class ShowVideoPreviewSteps : GreenCoffeeSteps() {

    private lateinit var testUri: Uri
    private lateinit var scenario: ActivityScenario<VideoPlayerActivity>

    @Given("^User has public video on device with a uri \"([^\"]*)\"\$")
    fun userHasPublicVideoOnDeviceWithAUri(uri: String) {
        testUri = Uri.parse(uri)
    }

    @And("^Video was opened in player screen$")
    fun videoWasOpenedInPlayerScreen() {
        // Launch video player activity
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val intent = Intent(context, VideoPlayerActivity::class.java).apply {
            putExtra(context.getString(R.string.extra_vid_uri),testUri)
        }

        scenario = ActivityScenario.launch(intent)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Video preview is shown$")
    fun videoPreviewIsShown() {
        // Test case fixture for simulating playback prepared by system
        val slotRightVolume = slot<Float>()
        val slotLeftVolume = slot<Float>()
        val mediaPlayer: MediaPlayer = mockk()

        // Stub mocked player
        every { mediaPlayer.setVolume(capture(slotLeftVolume),capture(slotRightVolume)) } answers {}

        scenario.onActivity {
            val videoPreviewFragment = it.supportFragmentManager.fragments.first() as VideoPreviewFragment
            val videoViewShadow = Shadows.shadowOf(
                videoPreviewFragment.view?.findViewById<VideoView>(R.id.videoView)
            )!!

            // Simulate system has prepared playback
            videoViewShadow.onPreparedListener.onPrepared(mediaPlayer)

            // Verify playback is started from test uri
            assertThat(videoViewShadow.videoURIString).isEqualTo(testUri.toString())
            assertThat(videoViewShadow.currentVideoState).isEqualTo(ShadowVideoView.START)
        }

        // Verify playback preview view is displayed
        onView(withId(R.id.videoView))
            .check(matches(isDisplayed()))
    }
}