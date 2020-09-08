package com.diskin.alon.visuals.videos.presentation.controller

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle.State
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.videos.presentation.R
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.android.synthetic.main.fragment_video_preview.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast
import org.robolectric.shadows.ShadowVideoView

/**
 * [VideoPreviewFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class VideoPreviewFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<VideoPreviewFragment>

    // Stub data
    private val videoUri: Uri = Uri.parse("test uri")
    private val slotRightVolume = slot<Float>()
    private val slotLeftVolume = slot<Float>()
    private val mediaPlayer: MediaPlayer = mockk()

    @Before
    fun setUp() {
        // Stub mocked player
        every { mediaPlayer.setVolume(capture(slotLeftVolume),capture(slotRightVolume)) } answers {}

        // Launch fragment under test
        val fragmentArgs = Bundle().apply {
            putParcelable(VideoPreviewFragment.KEY_VID_URI,videoUri)
        }
        val factory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                return if (loadFragmentClass(classLoader, className) == VideoPreviewFragment::class.java) {
                    VideoPreviewFragment()

                } else {
                    super.instantiate(classLoader, className)
                }
            }
        }
        scenario = FragmentScenario.launchInContainer(
            VideoPreviewFragment::class.java,
            fragmentArgs,
            factory
        )
    }

    @Test
    fun prepareVideoPlayBack_whenResumed() {
        // Given a resumed fragment

        // Then fragment should prepare video playback
        scenario.onFragment {
            val videoViewShadow = Shadows.shadowOf(it.videoView)!!

            assertThat(videoViewShadow.videoURIString).isEqualTo(videoUri.toString())
        }
    }

    @Test
    fun startMuteVideoPlayBack_whenPlaybackPrepared() {
        // Given a resumed fragment

        scenario.onFragment {
            val videoViewShadow = Shadows.shadowOf(it.videoView)!!

            // When video playback is prepared
            videoViewShadow.onPreparedListener.onPrepared(mediaPlayer)

            // Then fragment should play the video specified by uri passed as bundle arg
            assertThat(videoViewShadow.currentVideoState).isEqualTo(ShadowVideoView.START)
            assertThat(videoViewShadow.videoURIString).isEqualTo(videoUri.toString())

            // And playback should be muted
            assertThat(slotLeftVolume.captured).isZero()
            assertThat(slotRightVolume.captured).isZero()
        }
    }

    @Test
    fun stopVideoPlayBack_whenFragmentStopped() {
        // Given a resumed activity

        // When fragment is moved to stopped state
        scenario.moveToState(State.CREATED)

        // Then fragment should stop video playback
        scenario.onFragment {
            val videoViewShadow = Shadows.shadowOf(it.videoView)!!

            assertThat(videoViewShadow.currentVideoState).isEqualTo(ShadowVideoView.STOP)
        }
    }

    @Test
    fun replayVideoFromStart_whenVideoPlayBackCompletes() {
        // Given a resumed fragment

        scenario.onFragment {
            // And on going video playback
            it.videoView.setVideoURI(mockk())
            it.videoView.setOnCompletionListener { _ -> it.videoView.start() }

            // When playback completes
            val videoViewShadow = Shadows.shadowOf(it.videoView)!!
            videoViewShadow.onCompletionListener.onCompletion(mockk())

            // Then fragment should replay video from start
            assertThat(it.videoView.isPlaying).isTrue()
        }
    }

    @Test
    fun playVideoFromStart_whenRecreated() {
        // Given a resumed fragment

        // And video playback has been playing
        scenario.onFragment {
            val videoViewShadow = Shadows.shadowOf(it.videoView)!!

            videoViewShadow.onPreparedListener.onPrepared(mediaPlayer)
            it.videoView.seekTo(500)
        }

        // When fragment is recreated
        scenario.recreate()

        scenario.onFragment {
            // And playback is prepared
            val videoViewShadow = Shadows.shadowOf(it.videoView)!!
            videoViewShadow.onPreparedListener.onPrepared(mediaPlayer)

            // Then fragment should start video playback from the beginning
            assertThat(it.videoView.currentPosition).isEqualTo(0)
        }
    }

    @Test
    fun playVideoWithDevicePlayer_whenUserPlayVideoAndDeviceHasPlayer() {
        // Test case fixture
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val shadowPackageManager = Shadows.shadowOf(
            context.packageManager)!!
        val component = ComponentName("com.example", "Example")

        shadowPackageManager.addActivityIfNotPresent(component)
        shadowPackageManager.addIntentFilterForActivity(component,
            IntentFilter(Intent.ACTION_VIEW).apply {
                addDataType("video/*")
                addCategory(Intent.CATEGORY_DEFAULT)
            }
        )

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Given a resumed fragment and an existing video player app on device

        // When user selects to play the video
        onView(withId(R.id.playVideoButton))
            .perform(click())

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should ask system to play video with an available player app(implicit intent)
        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(videoUri))
        hasType(context.getString(R.string.video_mime_type))

        // Release test case resources
        Intents.release()
    }

    @Test
    fun showErrorMessage_whenUserPlayVideoAndDeviceHasNoPlayer() {
        // Given a resumed fragment and an existing video player app on device

        // When user selects to play the video
        onView(withId(R.id.playVideoButton))
            .perform(click())

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should notify user with toast message
        val toastMessage = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.no_player_error)
        assertThat(ShadowToast.getTextOfLatestToast().toString()).isEqualTo(toastMessage)
    }
}