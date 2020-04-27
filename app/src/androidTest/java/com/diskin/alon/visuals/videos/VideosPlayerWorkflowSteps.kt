package com.diskin.alon.visuals.videos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.util.isVideoViewPlayingWithUri
import com.diskin.alon.visuals.videos.presentation.controller.VideosAdapter.VideoViewHolder
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.allOf

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

        // Delete test pictures from test device storage
        getTestVideosUri().forEach {
            ApplicationProvider.getApplicationContext<Context>().contentResolver
                .delete(it,null,null)
        }
    }
}