package com.diskin.alon.visuals.recyclebin

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashItemsAdapter.TrashItemViewHolder
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItemType
import com.diskin.alon.visuals.util.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.util.isRecyclerViewItemsCount
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf

/**
 * Step definitions for the 'User restores trashed items' scenario.
 */
class RestoreTrashedWorkflowSteps : RecycleBinWorkflowsBackground() {

    private lateinit var restoredVideoUri: Uri

    @Given("^User has trashed items from device videos and pictures$")
    override fun userHasTrashedItemsFromDeviceVideosAndPictures() {
        super.userHasTrashedItemsFromDeviceVideosAndPictures()
    }

    @And("^User launch app from device home screen$")
    override fun userLaunchAppFromDeviceHomeScreen() {
        super.userLaunchAppFromDeviceHomeScreen()
    }

    @When("^User navigates to recycle bin screen$")
    override fun userNavigatesToRecycleBinScreen() {
        super.userNavigatesToRecycleBinScreen()
    }

    @And("^Restores first listed video trash item$")
    fun restoresFirstListedVideoTrashItem() {
        // Find first video position for expected shown trash items
        val selectedVideoPosition = expectedTrashedItems
            .indexOfFirst { it.type == TrashItemType.VIDEO }
        restoredVideoUri = expectedTrashedItems
            .find { it.type == TrashItemType.VIDEO }!!.uri

        // Update expected shown trash items
        expectedTrashedItems.removeAt(selectedVideoPosition)

        // Select first displayed video in trash
        onView(withId(R.id.trashList))
            .perform(actionOnItemAtPosition<TrashItemViewHolder>(selectedVideoPosition,longClick()))

        // Restore selected video
        val context = ApplicationProvider.getApplicationContext<Context>()
        onView(withContentDescription(context.getString(R.string.action_restore)))
            .perform(click())
    }

    @Then("^Video should be restored to videos browser$")
    fun videoShouldBeRestoredToVideosBrowser() {
        // Verify recycle bin shows all expected items,post video restoration
        val expectedSize = expectedTrashedItems.size

        onView(withId(R.id.trashList))
            .check(matches(isRecyclerViewItemsCount(expectedSize)))

        expectedTrashedItems.forEachIndexed { index, trashedItem ->
            val viewId = when(trashedItem.type) {
                TrashItemType.VIDEO -> R.id.trashedVideoThumb
                TrashItemType.PICTURE -> R.id.trashedPicture
            }
            onView(withRecyclerView(R.id.trashList).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(viewId),
                                withTagValue(`is`(trashedItem.uri.toString())),
                                isDisplayed()
                            )
                        )
                    )
                )
        }


        // Open videos browser screen
        onView(withId(R.id.videos))
            .perform(click())

        // Verify videos browser is showing the restored video with all other device videos
        onView(
            allOf(
                withId(R.id.videoThumb),
                withTagValue(`is`(restoredVideoUri.toString()))
            )
        )
            .check(matches(isDisplayed()))
    }

    @When("^User restore all items in trash$")
    fun userRestoreAllItemsInTrash() {
        // Return to recycle bin browser ui
        onView(withId(R.id.recycle_bin))
            .perform(click())

        // Open options menu
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        // Select 'restore all' menu item
        onView(withText(R.string.action_restore_all))
            .perform(click())

        // Approve dialog
        onView(withText(R.string.dialog_pos_label))
            .inRoot(RootMatchers.isDialog())
            .perform(click())
    }

    @Then("^All items should be restored$")
    fun allItemsShouldBeRestored() {
        // Verify screen do not show any listed trash items
        onView(withId(R.id.trashList))
            .check(matches(isRecyclerViewItemsCount(0)))
    }
}