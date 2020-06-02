package com.diskin.alon.visuals.videos

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.util.DeviceUtil
import com.diskin.alon.visuals.util.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.util.isRecyclerViewItemsCount
import com.diskin.alon.visuals.videos.data.MediaStoreVideoProvider
import com.diskin.alon.visuals.videos.presentation.controller.VideosAdapter.VideoViewHolder
import com.diskin.alon.visuals.videos.presentation.model.VideoDuration
import com.google.common.truth.Truth
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.*

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
                                    withText(getExpectedVideoDuration(index).getFormattedDuration()),
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
                actionOnItemAtPosition<VideoViewHolder>(
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

        // Exit from android share sheet ui
        DeviceUtil.pressBack()
    }

    @When("^User selects videos for trashing$")
    fun userSelectsVideosForTrashing() {
        // Select videos to trash from ui
        onView(allOf(
            instanceOf(ActionMenuItemView::class.java),
            withContentDescription(R.string.action_trash_title)))
            .perform(click())

        // Confirm trashing alert dialog
        onView(withText(R.string.dialog_pos_label))
        .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
    }

    @Then("^Videos should be moved to recycle bin$")
    fun videosShouldBeMovedToRecycleBin() {
        // Verify trashed videos are not displayed in videos browser any more
        onView(withId(R.id.videosList))
            .check(matches(isRecyclerViewItemsCount(1)))

        onView(withRecyclerView(R.id.videosList).atPosition(0))
            .check(
                matches(
                    allOf(
                        hasDescendant(
                            allOf(
                                withId(R.id.videoThumb),
                                withTagValue(`is`(getTestVideosUri()[1].toString())),
                                isDisplayed()
                            )
                        ),
                        hasDescendant(
                            allOf(
                                withId(R.id.videoDuration),
                                withText(getExpectedVideoDuration(1).getFormattedDuration()),
                                isDisplayed()
                            )
                        )
                    )
                )
            )

        // verify trashed videos are in app recycle bin
        onView(withId(R.id.recycle_bin))
            .perform(click())

        onView(withId(R.id.trashedList))
            .check(matches(isRecyclerViewItemsCount(1)))

        onView(withRecyclerView(R.id.trashedList).atPosition(0))
            .check(
                matches(
                    hasDescendant(
                        allOf(
                            withId(R.id.trashedVideoThumb),
                            withTagValue(`is`(getTestVideosUri()[0].toString())),
                            isDisplayed()
                        )
                    )
                )
            )
    }

    private fun getExpectedVideoDuration(index: Int): VideoDuration {
        // Query provider
        val uri = getTestVideosUri()[index]
        val columnId = MediaStore.Video.VideoColumns._ID
        val columnDuration = MediaStore.Video.VideoColumns.DURATION
        val contentResolver = ApplicationProvider.getApplicationContext<Context>()
            .contentResolver
        val cursor = contentResolver.query(
            MediaStoreVideoProvider.VIDEOS_PROVIDER_URI,
            arrayOf(
                columnId,
                columnDuration
            ),
            "${MediaStore.Video.VideoColumns._ID} = ?",
            arrayOf(uri.lastPathSegment),
            null)!!

        // Extract columns data from cursor
        cursor.moveToNext()

        val videoDuration = cursor.getLong(cursor.getColumnIndex(columnDuration))


        return VideoDuration(
            (videoDuration / 1000 % 60).toInt(),
            (videoDuration / 1000 / 60).toInt()
        )
    }
}