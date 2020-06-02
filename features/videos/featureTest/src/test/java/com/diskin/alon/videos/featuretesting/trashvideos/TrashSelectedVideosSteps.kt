package com.diskin.alon.videos.featuretesting.trashvideos

import android.net.Uri
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
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
 * Step definitions for the 'User trash selected videos' scenario.
 */
class TrashSelectedVideosSteps(
    mockedVideosProvider: DeviceMediaProvider<MediaStoreVideo>,
    private val testDao: TrashedItemDao
) : TrashVideosStepsBackground(mockedVideosProvider) {

    private val testTrashedVideos = mutableListOf<TrashedItemEntity>()

    @Given("^User has public videos on device$")
    override fun userHasPublicVideosOnDevice(rows: List<TableRow>) {
        super.userHasPublicVideosOnDevice(rows)
    }

    @And("^User open videos browser screen$")
    fun userOpenVideosBrowserScreen() {
        openVideosBrowserScreen()
    }

    @Then("^Videos should be displayed by date added,in descending order$")
    fun videosShouldBeDisplayedByDateAddedInDescendingOrder(rows: List<TableRow>) {
        verifyTestDataVideosDisplayed(rows)
    }

    @When("^User selects the next videos$")
    fun userSelectsTheNextVideos(rows: List<TableRow>) {
        // Extract test data
        val testData = rows.toMutableList()
        val uriIndex = 0

        testData.removeAt(0)
        testData.forEachIndexed { index, row ->
            val testUri = row.cells[uriIndex].value!!
            val action: ViewAction = if (index == 0) longClick() else click()

            // Add extracted to trashed list
            testTrashedVideos.add(
                TrashedItemEntity(
                    testUri,
                    TrashedEntityType.VIDEO
                )
            )

            // Select video in ui for trashing
            onView(withId(R.id.videosList))
                .perform(actionOnHolderItem(withVideoUri(Uri.parse(testUri)),action))
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^Select to trash them$")
    fun selectsToTrashThem() {
        // Trash selected videos
        onView(withContentDescription(R.string.action_trash_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Browser should require additional user confirmation for trashing$")
    fun browserShouldRequireAdditionalConfirmationForTrashing() {
        // Verify alert dialog is showing
        (ShadowAlertDialog.getLatestDialog() as AlertDialog).apply {
            assertThat(isShowing).isTrue()
        }
    }

    @When("^User approve trashing$")
    fun userApproveTrashing() {
        // Approve trashing by clicking on positive dialog button
        (ShadowAlertDialog.getLatestDialog() as AlertDialog).apply {
            getButton(AlertDialog.BUTTON_POSITIVE).performClick()
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    @Then("^Selected trashed videos should be moved to app recycle bin$")
    fun selectedTrashedVideosShouldBeMovedToAppRecycleBin() {
        // Verify selected videos were trashed
        assertThat(testDao.getAll().blockingFirst()).isEqualTo(testTrashedVideos)
    }

    @And("^Videos browser should update shown videos,to exclude trashed videos$")
    fun videosBrowserShouldUpdateShownVideosToExcludeTrashedVideos(rows: List<TableRow>) {
        verifyTestDataVideosDisplayed(rows)
    }
}