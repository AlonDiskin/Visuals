package com.diskin.alon.videos.featuretesting.trashvideos

import android.os.Looper
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import org.robolectric.Shadows

/**
 * Step definitions for the 'Restored videos are re displayed' scenario.
 */
class UpdateRestoredVideosSteps(
    private val mockedVideosProvider: DeviceMediaProvider<MediaStoreVideo>,
    private val testDao: TrashedItemDao
) : TrashVideosStepsBackground(mockedVideosProvider) {

    @Given("^User has public videos on device$")
    override fun userHasPublicVideosOnDevice(rows: List<TableRow>) {
        super.userHasPublicVideosOnDevice(rows)
    }

    @And("^Videos in app recycle bin$")
    fun videosInAppRecycleBin(rows: List<TableRow>) {
        // Extract test data and set test videos to recycle bin
        val testData = rows.toMutableList()
        val uriIndex = 0

        // Remove first row containing column names
        testData.removeAt(0)

        testData.forEach { row ->
            val testUri = row.cells[uriIndex].value!!

            testDao.insert(
                TrashedItemEntity(
                    testUri,
                    TrashedEntityType.VIDEO
                )
            )
                .blockingAwait()
        }
    }

    @And("^User open videos browser screen$")
    fun userOpenVideosBrowserScreen() {
        openVideosBrowserScreen()
    }

    @When("^Trashed videos are restored in recycle bin$")
    fun trashedVideosAreRestoredInRecycleBin() {
        // Remove trashed videos from recycle bin
        val trashed = testDao.getAll().blockingFirst()!!
        testDao.delete(*trashed.toTypedArray()).blockingAwait()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        // TODO find a way to sync test thread with list adapter async diff util thread
        Thread.sleep(500L)
    }

    @Then("^Videos browser should update shown videos,to include restored videos$")
    fun videosBrowserShouldUpdateShownVideosToIncludeRestoredVideos(rows: List<TableRow>) {
        verifyTestDataVideosDisplayed(rows)
    }
}