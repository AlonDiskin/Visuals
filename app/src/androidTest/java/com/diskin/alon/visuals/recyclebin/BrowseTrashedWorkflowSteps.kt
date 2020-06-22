package com.diskin.alon.visuals.recyclebin

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItemType
import com.diskin.alon.visuals.util.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.util.isRecyclerViewItemsCount
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf

/**
 * Step definitions for the 'Trashed items are listed' scenario.
 */
class BrowseTrashedWorkflowSteps : RecycleBinWorkflowsBackground() {

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

    @Then("^All trashed items should be shown sorted by trashing date in desc order$")
    fun allTrashedItemsShouldBeShownSortedByTrashingDateInDescOrder() {
        // Verify test trashed items are displayed as expected
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
                                withTagValue(CoreMatchers.`is`(trashedItem.uri.toString())),
                                isDisplayed()
                            )
                        )
                    )
                )
        }
    }

    @When("^User filters items to show only trashed pictures$")
    fun userFiltersItemsToShowOnlyTrashedPictures() {
        onView(withId(R.id.action_filter))
            .perform(click())
        onView(withText(R.string.title_filter_image))
            .perform(click())
    }

    @Then("^Only trashed pictures should be displayed$")
    fun onlyTrashedPicturesShouldBeDisplayed() {
        val expectedSize = expectedTrashedItems
            .filter { it.type == TrashItemType.PICTURE }
            .size

        // Verify all expected items are shown
        expectedTrashedItems
            .filter { it.type == TrashItemType.PICTURE }
            .forEachIndexed { index, trashedItem ->
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
                                    withTagValue(CoreMatchers.`is`(trashedItem.uri.toString())),
                                    isDisplayed()
                                )
                            )
                        )
                    )
            }

        // Verify only expected items shown
        onView(withId(R.id.trashList))
            .check(matches(isRecyclerViewItemsCount(expectedSize)))
    }
}