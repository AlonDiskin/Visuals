package com.diskin.alon.visuals.videos

import android.content.Context
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.catalog.data.MediaStoreVideoProvider
import com.diskin.alon.visuals.catalog.presentation.controller.VideosAdapter.VideoViewHolder
import com.diskin.alon.visuals.catalog.presentation.model.VideoDetail
import com.diskin.alon.visuals.util.isVideoViewPlayingWithUri
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.allOf
import java.text.SimpleDateFormat
import java.util.*

/**
 * Step definitions for 'Videos player usage' scenario.
 */
class VideosPlayerWorkflowSteps : VideosWorkflowsStepsBackground() {

    @Given("^User has public videos on his device$")
    override fun userHasPublicVideosOnHisDevice() {
        super.userHasPublicVideosOnHisDevice()
    }

    @Given("^User launch app from device home screen$")
    override fun userLaunchAppFromDeviceHomeScreen() {
        super.userLaunchAppFromDeviceHomeScreen()
    }

    @And("^User navigates to videos browser screen$")
    override fun userNavigatesToVideosBrowserScreen() {
        super.userNavigatesToVideosBrowserScreen()
    }

    @When("^User selects the first listed video for playing$")
    fun userSelectsTheFirstListedVideoForPlaying() {
        // Select first video listed in browser
        onView(withId(R.id.videosList))
            .perform(scrollToPosition<VideoViewHolder>(0))
            .perform(actionOnItemAtPosition<VideoViewHolder>(
                    0,
                    click()
                )
            )
    }

    @Then("^Video playback preview is shown$")
    fun videoPlaybackPreviewIsShown() {
        // Verify the selected video playback is displayed in preview screen
        onView(withId(R.id.videoView))
            .check(matches(
                allOf(
                    isVideoViewPlayingWithUri(getTestVideosUri().first()),
                    isDisplayed()
                )
            ))
    }

    @When("^User selects to play video$")
    fun userSelectsToPlayVideo() {
        // Open video playback screen from preview screen
//        onView(withId(R.id.playVideoButton))
//            .perform(click())
    }

    @Then("^App should ask device to play video from an available player app$")
    fun appShouldAskDeviceToPlayVideoFromAnAvailablePlayerApp() {
        // Verify an implicit intent has been sent by app to system in order to play video
//        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
//        Intents.intended(IntentMatchers.hasData(getTestVideosUri().first()))
//        IntentMatchers.hasType("video/*")

        // Return to preview screen
        //DeviceUtil.pressBack()
    }

    @When("^User open video detail$")
    fun userOpenVideoDetail() {
        // Open video detail
        onView((withId(R.id.pager)))
            .perform(swipeUp())
    }

    @Then("^Video detail should be displayed$")
    fun videoDetailShouldBeDisplayed() {
        // Verify expected test video info is shown
        val expectedVideoDetail = getExpectedUiVideoDetail()

        onView(withId(R.id.sizeDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(expectedVideoDetail.size)
                    )
                )
            )

        onView(withId(R.id.dateDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(expectedVideoDetail.added)
                    )
                )
            )

        onView(withId(R.id.pathDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(expectedVideoDetail.path)
                    )
                )
            )

        onView(withId(R.id.titleDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(expectedVideoDetail.title)
                    )
                )
            )

        onView(withId(R.id.resolutionDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(expectedVideoDetail.resolution)
                    )
                )
            )

        onView(withId(R.id.durationDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(expectedVideoDetail.duration)
                    )
                )
            )
    }

    private fun getExpectedUiVideoDetail(): VideoDetail {
        val uri = getTestVideosUri().first()
        val columnId = MediaStore.Video.VideoColumns._ID
        val columnDate = MediaStore.Video.VideoColumns.DATE_ADDED
        val columnDuration = MediaStore.Video.VideoColumns.DURATION
        val columnSize = MediaStore.Video.VideoColumns.SIZE
        val columnTitle = MediaStore.Video.VideoColumns.TITLE
        val columnPath = MediaStore.Video.VideoColumns.DATA
        val columnWidth = MediaStore.Video.VideoColumns.WIDTH
        val columnHeight = MediaStore.Video.VideoColumns.HEIGHT

        // Query provider
        val contentResolver = ApplicationProvider.getApplicationContext<Context>()
            .contentResolver
        val cursor = contentResolver.query(
            MediaStoreVideoProvider.VIDEOS_PROVIDER_URI,
            arrayOf(
                columnId,
                columnDate,
                columnDuration,
                columnSize,
                columnTitle,
                columnPath,
                columnWidth,
                columnHeight
            ),
            "${MediaStore.Video.VideoColumns._ID} = ?",
            arrayOf(uri.lastPathSegment),
            null)!!

        // Extract columns data from cursor
        cursor.moveToNext()

        val videoDate =
            cursor.getLong(cursor.getColumnIndex(columnDate)) * 1000L // convert to milliseconds
        val videoDuration = cursor.getLong(cursor.getColumnIndex(columnDuration))
        val videoSize = cursor.getLong(cursor.getColumnIndex(columnSize))
        val videoTitle = cursor.getString(cursor.getColumnIndex(columnTitle))!!
        val videoPath = cursor.getString(cursor.getColumnIndex(columnPath))!!
        val videoWidth = cursor.getLong(cursor.getColumnIndex(columnWidth))
        val videoHeight = cursor.getLong(cursor.getColumnIndex(columnHeight))


        return VideoDetail(
            "${(videoSize.toDouble() / 1000000)}MB",
            SimpleDateFormat("dd MMMM yyyy HH:mm")
                .format(Date(videoDate)),
            String.format("%02d:%02d", (videoDuration / 1000 / 60).toInt(),
                (videoDuration / 1000 % 60).toInt()),
            videoPath,
            videoTitle,
            "${videoWidth.toInt()}*${videoHeight.toInt()}"
        )
    }

    fun deleteTestVideosFromDevice() {
        // Delete test pictures from test device storage
        getTestVideosUri().forEach {
            ApplicationProvider.getApplicationContext<Context>().contentResolver
                .delete(it,null,null)
        }
    }
}