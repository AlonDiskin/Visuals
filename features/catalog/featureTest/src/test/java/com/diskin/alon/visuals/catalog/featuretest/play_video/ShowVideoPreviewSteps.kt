package com.diskin.alon.visuals.catalog.featuretest.play_video

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Looper
import android.widget.VideoView
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.catalog.data.MediaStoreVideo
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.catalog.presentation.controller.VideoDetailActivity
import com.diskin.alon.visuals.catalog.presentation.controller.VideoPreviewFragment
import com.diskin.alon.visuals.catalog.presentation.controller.VideosAdapter.VideoViewHolder
import com.diskin.alon.visuals.catalog.presentation.controller.VideosBrowserFragment
import com.google.common.truth.Truth
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowVideoView

/**
 * Step definitions of the 'Video preview is shown' scenario.
 */
class ShowVideoPreviewSteps(
    private val mockedVideosProvider: DeviceMediaProvider<MediaStoreVideo>
) : GreenCoffeeSteps() {

    private lateinit var testUri: Uri
    private lateinit var testDeviceVideos: MutableList<MediaStoreVideo>
    private lateinit var testDeviceVideosSubject: Subject<List<MediaStoreVideo>>
    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext())
    private lateinit var videoBrowserScenario: FragmentScenario<VideosBrowserFragment>
    private lateinit var videoDetailScenario: ActivityScenario<VideoDetailActivity>

    @Given("^User has public video on device with a uri \"([^\"]*)\"$")
    fun userHasPublicVideoOnDeviceWithAUri(uri: String) {
        // Create test fixture according to step and test data

        // Setup test video data
        testUri = Uri.parse(uri)
        testDeviceVideos = mutableListOf(
            MediaStoreVideo(
                testUri,
                0L,
                0L
            )
        )
        testDeviceVideosSubject = BehaviorSubject.createDefault(testDeviceVideos)

        // Stub mocked provider to return test data
        every{ mockedVideosProvider.getAll() } returns testDeviceVideosSubject

        // Setup test nav controller
        navController.setGraph(R.navigation.videos_nav_graph)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.videoDetail) {
                val context = ApplicationProvider.getApplicationContext<Context>()!!
                val intent = Intent(context, VideoDetailActivity::class.java).apply {
                    // Extract vid uri from controller and put it to activity launch intent
                    val vidUriArgKey = context.getString(R.string.extra_vid_uri)
                    val vidUriArg = navController
                        .currentBackStackEntry?.arguments?.getParcelable<Uri>(vidUriArgKey)!!

                    putExtra(vidUriArgKey,vidUriArg)
                }

                // Launch video detail activity when user navigates to it from browser(manually,robolectric bug)
                videoDetailScenario = ActivityScenario.launch(intent)

                // Wait for main looper to idle
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }
        }
    }

    @And("^User selects video from browser$")
    fun userSelectsVideoFromBrowser() {
        // Launch videos browser fragment
        videoBrowserScenario = FragmentScenario.launchInContainer(VideosBrowserFragment::class.java)
        // Set test nav controller on videos fragment
        videoBrowserScenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Select test video
        onView(withId(R.id.videosList))
            .perform(
                actionOnItemAtPosition<VideoViewHolder>(0, click())
            )

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Video preview should be displayed in own screen$")
    fun videoPreviewShouldBeDisplayedInOwnScreen() {
        // Test case fixture for simulating playback prepared by system
        val slotRightVolume = slot<Float>()
        val slotLeftVolume = slot<Float>()
        val mediaPlayer: MediaPlayer = mockk()

        // Stub mocked player
        every { mediaPlayer.setVolume(capture(slotLeftVolume),capture(slotRightVolume)) } answers {}

        videoDetailScenario.onActivity {
            // Verify preview fragment is displayed and stub its video view
            val videoPreviewFragment = it.supportFragmentManager.fragments.first()
                    as VideoPreviewFragment
            val videoViewShadow = Shadows.shadowOf(
                videoPreviewFragment.view?.findViewById<VideoView>(R.id.videoView)
            )!!

            // Simulate system has prepared playback for preview video view
            videoViewShadow.onPreparedListener.onPrepared(mediaPlayer)

            // Verify playback is started from test uri
            Truth.assertThat(videoViewShadow.videoURIString).isEqualTo(testUri.toString())
            Truth.assertThat(videoViewShadow.currentVideoState).isEqualTo(ShadowVideoView.START)
        }

        // Verify playback preview view is displayed
        onView(withId(R.id.videoView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}