package com.diskin.alon.visuals.videos

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.util.DeviceUtil
import com.diskin.alon.visuals.util.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.videos.presentation.controller.VideosAdapter
import com.google.common.truth.Truth
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf

/**
 * Step definitions for videos browser feature workflow scenario.
 */
class VideosBrowserWorkflowSteps : VideosWorkflowsStepsBackground() {

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

    @Then("^All user device public videos should be shown by date in descending order$")
    fun allUserDevicePublicVideosShouldBeShownByDateInDescendingOrder() {
        // Verify all test videos are shown sorted by date added in descending order
        getTestVideosUri().forEachIndexed { index, uri ->
            onView(withRecyclerView(R.id.videosList).atPosition(index))
                .check(
                    matches(
                        allOf(
                            hasDescendant(
                                allOf(
                                    withId(R.id.videoThumb),
                                    withTagValue(`is`(uri.toString())),
                                    isDisplayed()
                                )
                            ),
                            hasDescendant(
                                allOf(
                                    withId(R.id.videoDuration),
                                    withText(getTestDurations()[index].getFormattedDuration()),
                                    isDisplayed()
                                )
                            )
                        )
                    )
                )
        }
    }

    @When("^User rotates device$")
    fun userRotatesDevice() {
        // Rotate test device
        DeviceUtil.rotateDeviceLand()
    }

    @Then("^Videos are displayed as before$")
    fun picturesAreDisplayedAsBefore() {
        // Verify all test videos are shown sorted by date added in descending order
        getTestVideosUri().forEachIndexed { index, uri ->
            onView(withRecyclerView(R.id.videosList).atPosition(index))
                .check(
                    matches(
                        allOf(
                            hasDescendant(
                                allOf(
                                    withId(R.id.videoThumb),
                                    withTagValue(`is`(uri.toString())),
                                    isDisplayed()
                                )
                            ),
                            hasDescendant(
                                allOf(
                                    withId(R.id.videoDuration),
                                    withText(getTestDurations()[index].getFormattedDuration()),
                                    isDisplayed()
                                )
                            )
                        )
                    )
                )
        }
    }

    @When("^User selects the first listed video for sharing$")
    fun userSelectsFirstListedVideoForSharing() {
        // Select first listed video
        onView(withId(R.id.videosList))
            .perform(
                actionOnItemAtPosition<VideosAdapter.VideoViewHolder>(
                    0,
                    longClick()
                )
            )

        // Click on share menu item
        onView(withContentDescription(R.string.action_share_title))
            .perform(click())
    }

    @Then("^App should share video$")
    fun appShouldShareVideo() {
        // Verify sharing intent is using the android sharing ui sheet
        val expectedIntentPosition = 1

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        // Verify selected video uri is included in sharing intent
        val intent = Intents.getIntents()[expectedIntentPosition].extras.get(Intent.EXTRA_INTENT) as Intent
        val intentUri = intent.extras.getParcelable<Uri>(Intent.EXTRA_STREAM)!!

        Truth.assertThat(intent.action).isEqualTo(Intent.ACTION_SEND)
        Truth.assertThat(intent.type).isEqualTo("video/*")
        Truth.assertThat(intentUri).isEqualTo(getTestVideosUri().first())

        // Delete test pictures from test device storage(if needed)
        getTestVideosUri().forEach {
            ApplicationProvider.getApplicationContext<Context>().contentResolver
                .delete(it,null,null)
        }

        // Exit from android share sheet ui
        DeviceUtil.pressBack()
    }
}