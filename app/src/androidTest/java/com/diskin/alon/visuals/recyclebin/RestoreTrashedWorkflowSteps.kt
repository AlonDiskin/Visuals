package com.diskin.alon.visuals.recyclebin

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.util.isRecyclerViewItemsCount
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When

/**
 * Step definitions for the 'User restores trashed items' scenario.
 */
class RestoreTrashedWorkflowSteps : RecycleBinWorkflowsBackground() {

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