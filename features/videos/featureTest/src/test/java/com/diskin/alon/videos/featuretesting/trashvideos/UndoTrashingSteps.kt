package com.diskin.alon.videos.featuretesting.trashvideos

import android.net.Uri
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.videos.featuretesting.util.withVideoUri
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.presentation.R
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowAlertDialog

/**
 * Steps definitions for 'User undo videos trashing' scenario.
 */
class UndoTrashingSteps(
    mockedVideosProvider: DeviceMediaProvider<MediaStoreVideo>,
    private val testTrashedDao: TrashedItemDao
) : TrashVideosStepsBackground(mockedVideosProvider){

    @Given("^User has public videos on device$")
    override fun userHasPublicVideosOnDevice(rows: List<TableRow>) {
        super.userHasPublicVideosOnDevice(rows)
    }

    @And("^User open videos browser screen$")
    fun userOpenVideosBrowserScreen() {
        openVideosBrowserScreen()
    }

    @When("^User selects the next videos$")
    fun userSelectsTheNextVideos(rows: List<TableRow>) {
        // Extract test data
        val testData = rows.toMutableList()
        val uriIndex = 0

        testData.removeAt(0)
        testData.forEachIndexed { index, row ->
            val testUri = row.cells[uriIndex].value!!
            val action: ViewAction = if (index == 0) ViewActions.longClick() else click()

            // Select video in ui for trashing
            onView(ViewMatchers.withId(R.id.videosList))
                .perform(
                    RecyclerViewActions.actionOnHolderItem(
                        withVideoUri(Uri.parse(testUri)),
                        action
                    )
                )
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^Trash them$")
    fun trashThem() {
        // Trash selected videos
        onView(ViewMatchers.withContentDescription(R.string.action_trash_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Approve trashing by clicking on positive dialog button
        (ShadowAlertDialog.getLatestDialog() as AlertDialog).apply {
            getButton(AlertDialog.BUTTON_POSITIVE).performClick()
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User undo trashing$")
    fun userUndoTrashing() {
        // Undo trashing
        onView(ViewMatchers.withId(R.id.snackbar_action))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Videos browser should restore trashed from recycle bin$")
    fun videosBrowserShouldRestoreTrashedFromRecycleBin() {
        // Verify trashed dao is empty
        assertThat(testTrashedDao.getAll().blockingFirst()).isEmpty()
    }

    @And("^All device videos should be by date added,in descending order$")
    fun allDeviceVideosShouldBeByDateAddedInDescendingOrder(rows: List<TableRow>) {
        verifyTestDataVideosDisplayed(rows)
    }
}