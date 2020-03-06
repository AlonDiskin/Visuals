package com.diskin.alon.visuals.videos.presentation

import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.view.ActionMode
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.videos.presentation.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.videos.presentation.VideosAdapter.VideoViewHolder
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
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

    // Collaborator stub data
    private val videosLiveData = MutableLiveData<List<Video>>()
    private val updateFailEvent = SingleLiveEvent<String>()
    private val testVideos = arrayListOf(
        Video(Uri.parse("uri 1"), VideoDuration(10,2)),
        Video(Uri.parse("uri 2"),VideoDuration(40,0)),
        Video(Uri.parse("uri 3"),VideoDuration(5,1)),
        Video(Uri.parse("uri 4"),VideoDuration(35,2)),
        Video(Uri.parse("uri 5"),VideoDuration(56,0)),
        Video(Uri.parse("uri 6"),VideoDuration(15,1)),
        Video(Uri.parse("uri 7"),VideoDuration(45,1)),
        Video(Uri.parse("uri 8"),VideoDuration(30,0))
    )

    @Before
    fun setUp() {
        // Mock out dagger injection
        mockkStatic(AndroidSupportInjection::class)

        // Mock out Glide
        mockkObject(ThumbnailLoader)

        val fragmentSlot = slot<Fragment>()

        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val videosFragment = fragmentSlot.captured as VideosFragment
            videosFragment.viewModel = viewModel
        }

        // Stub mocked collaborator behaviour
        every{ viewModel.videos } returns videosLiveData
        every { viewModel.videosUpdateFail } returns updateFailEvent

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(
            VideosFragment::class.java,
            null,
            R.style.AppTheme,
            null)
    }

    @Test
    fun showVideos_whenVideosUpdated() {
        // Given a resumed fragment

        // When view model update videos
        displayTestVideos()

        // Then fragment should show updated videos in layout
        testVideos.forEachIndexed { index, video ->
            // Verify ThumbnailLoader has been invoked to load test video thumbnail
            verify { ThumbnailLoader.load(any(),video.uri) }

            // Scroll to position matching test videos index and verify that video at
            // position shows expected info
            scrollToVideoAndCheck(
                index,
                hasDescendant(
                    allOf(
                        withId(R.id.videoDuration),
                        isDisplayed())
                ))
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

    @Test
    fun showVideoSelectionUi_whenUserStartVideoMultiSelection() {
        // Given a resumed fragment

        // And displayed videos
        displayTestVideos()

        // When user long clicks on a video
        val selectedVideoIndex = 0

        scrollToVideoAndPerform(selectedVideoIndex, longClick())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show selectable foreground ui for each video
        testVideos.forEachIndexed { index, _ ->
            scrollToVideoAndCheck(
                index,
                allOf(
                    hasDescendant(
                        allOf(
                            withId(R.id.selectable_foreground),
                            withEffectiveVisibility(Visibility.VISIBLE)
                        )
                    ),
                    hasDescendant(
                        allOf(
                            withId(R.id.select_item_checkBox),
                            withEffectiveVisibility(Visibility.VISIBLE)
                        )
                    )
                ))
        }

        // And display contextual action bar with expected menu
        scenario.onFragment {
            val field = VideosFragment::class.java.getDeclaredField("actionMode")
            field.isAccessible = true
            val actionMode: ActionMode =  field.get(it) as ActionMode

            assertThat(actionMode.menu.getItem(0).title)
                .isEqualTo(it.getString(R.string.action_share_title))
            assertThat(actionMode.menu.getItem(0).isEnabled)
                .isTrue()
        }
    }

    @Test
    fun markVideos_whenMultiSelected() {
        // Given a resumed fragment

        // And displayed videos
        displayTestVideos()

        // When user selects some of the displayed videos
        val selectedVideosIndex = listOf(0,testVideos.size - 1)

        selectDisplayedVideos(selectedVideosIndex)

        // Then fragment should check selected videos only
        testVideos.forEachIndexed { index, _ ->
            val check = if (selectedVideosIndex.contains(index)) {
                hasDescendant(
                    allOf(
                        withId(R.id.select_item_checkBox),
                        isChecked(),
                        isDisplayed())
                )
            } else{
                hasDescendant(
                    allOf(
                        withId(R.id.select_item_checkBox),
                        isNotChecked(),
                        isDisplayed())
                )
            }

            scrollToVideoAndCheck(index,check)
        }
    }

    @Test
    fun unMarkVideo_whenUnSelected() {
        // Given a resumed fragment

        // And displayed videos
        displayTestVideos()

        // When user selects videos
        val selectedVideosIndex = listOf(0,2)
        val unSelectedIndex = selectedVideosIndex.last()

        selectDisplayedVideos(selectedVideosIndex)

        // And un select a video item
        scrollToVideoAndPerform(unSelectedIndex,click())

        // Then fragment should un mark the un selected video
        scrollToVideoAndCheck(
            unSelectedIndex,
            hasDescendant(
                allOf(
                    withId(R.id.select_item_checkBox),
                    isNotChecked(),
                    isDisplayed())
            ))
    }

    @Test
    fun shareVideos_whenUserSharesSelectedVideos() {
        // Test case fixture
        Intents.init()

        // Given a resumed fragment

        // And displayed videos
        displayTestVideos()

        // When user selects videos
        val selectedVideosIndex = listOf(2,3,5)

        selectDisplayedVideos(selectedVideosIndex)

        // And clicks on share menu item
        onView(withContentDescription(R.string.action_share_title))
            .perform(click())

        // Then fragment should share via implicit intent action

        // Verify fragment end share intent using android sharing sheet ui(ui chooser)
        intended(hasAction(Intent.ACTION_CHOOSER))
        intended(hasExtraWithKey(Intent.EXTRA_INTENT))

        // Verify share intent has the expected action,type, and contains the selected video uri's
        val intent = Intents.getIntents().first().extras.get(Intent.EXTRA_INTENT) as Intent
        val intentUris = intent.extras.getParcelableArrayList<Uri>(Intent.EXTRA_STREAM)!!

        assertThat(intent.action).isEqualTo(Intent.ACTION_SEND_MULTIPLE)
        assertThat(intent.type).isEqualTo("video/*")
        assertThat(intentUris.size).isEqualTo(selectedVideosIndex.size)
        selectedVideosIndex.forEachIndexed { _, testVideosIndex ->
            assertThat(intentUris.contains(testVideos[testVideosIndex].uri))
                .isTrue()
        }

        Intents.release()
    }

    @Test
    fun restoreUiState_whenRecreated() {
        // Given a resumed fragment

        // And displayed videos
        displayTestVideos()

        // When user selects videos
        val selectedVideosIndex = listOf(1,2,4,7)

        selectDisplayedVideos(selectedVideosIndex)

        // And fragment is recreated
        scenario.recreate()

        // Then fragment should restore its ui state
        testVideos.forEachIndexed { index, _ ->
            val check = if (selectedVideosIndex.contains(index)) {
                hasDescendant(
                    allOf(
                        withId(R.id.select_item_checkBox),
                        isChecked(),
                        isDisplayed())
                )
            } else{
                hasDescendant(
                    allOf(
                        withId(R.id.select_item_checkBox),
                        isNotChecked(),
                        isDisplayed())
                )
            }

            scrollToVideoAndCheck(index,check)
        }
    }

    @Test
    fun closeSelectionUi_whenAllVideosUnSelected() {
        // Given a resumed fragment

        // And displayed videos
        displayTestVideos()

        // When user start multi selection by long click on displayed video item
        scrollToVideoAndPerform(0, longClick())

        // And then un select the same item
        scrollToVideoAndPerform(0, click())

        // Then fragment should close multi select ui
        scenario.onFragment {
            val field = VideosFragment::class.java.getDeclaredField("actionMode")
            field.isAccessible = true
            val actionMode: ActionMode =  field.get(it) as ActionMode

            assertThat(actionMode.menu.hasVisibleItems())
                .isTrue()
        }

        testVideos.forEachIndexed { index, _ ->
            scrollToVideoAndCheck(
                index,
                hasDescendant(
                    allOf(
                        withId(R.id.selectable_foreground),
                        not(isDisplayed()))
                )
            )
        }
    }

    private fun selectDisplayedVideos(selectedVideosIndex: List<Int>) {
        selectedVideosIndex.forEachIndexed { index, testVideosIndex ->
            // If this is the first selected video, initiate multi selection and select,
            // else just select video
            val action: ViewAction = if (index == 0) longClick() else click()

            scrollToVideoAndPerform(testVideosIndex,action)
        }
    }

    private fun scrollToVideo(position: Int) {
        // Scroll to requested videos list position
        onView(withId(R.id.videosList))
            .perform(scrollToPosition<VideoViewHolder>(position))
    }

    private fun scrollToVideoAndCheck(position: Int, viewMatcher: Matcher<View>) {
        // Scroll to requested videos list position
        scrollToVideo(position)

        // Perform requested checking on view at position
        onView(withRecyclerView(R.id.videosList).atPosition(position))
            .check(matches(viewMatcher))
    }

    private fun scrollToVideoAndPerform(position: Int, viewAction: ViewAction) {
        onView(withId(R.id.videosList))
            .perform(scrollToPosition<VideoViewHolder>(position))
            .perform(actionOnItemAtPosition<VideoViewHolder>(position, viewAction))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    private fun displayTestVideos() {
        videosLiveData.value = testVideos

        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
}