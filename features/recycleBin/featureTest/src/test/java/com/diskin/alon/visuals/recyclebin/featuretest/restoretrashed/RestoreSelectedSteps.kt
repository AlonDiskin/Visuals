package com.diskin.alon.visuals.recyclebin.featuretest.restoretrashed

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.recuclebin.presentation.util.loadImage
import com.diskin.alon.visuals.recuclebin.presentation.util.loadThumbnail
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import com.diskin.alon.visuals.recyclebin.featuretest.R
import com.diskin.alon.visuals.recyclebin.featuretest.util.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.recyclebin.featuretest.util.withTrashItemUri
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import io.mockk.verify
import java.text.SimpleDateFormat
import java.util.*

/**
 * Step definitions for the 'User restore selected items' scenario.
 */
class RestoreSelectedSteps(
    private val testDao: TrashedItemDao,
    mockedMediaProvider: DeviceMediaProvider<MediaStoreVisual>
) : RestoreTrashedStepsBackground(testDao, mockedMediaProvider) {

    @Given("^User has public media on device$")
    override fun userHasPublicMediaOnDevice(rows: List<TableRow>) {
        super.userHasPublicMediaOnDevice(rows)
    }

    @And("^User hes media items in app recycle bin$")
    override fun userHesMediaItemsInAppRecycleBin(rows: List<TableRow>) {
        super.userHesMediaItemsInAppRecycleBin(rows)
    }

    @When("^User opens recycle bin screen$")
    override fun userOpensRecycleBinScreen() {
        super.userOpensRecycleBinScreen()
    }

    @And("^Selects the next trashed items$")
    fun selectsTheNextTrashedItems(rows: List<TableRow>) {
        // Extract test data
        val testData = rows.toMutableList()
        val uriIndex = 0

        testData.removeAt(0)
        testData.forEachIndexed { index, row ->
            val testUri = row.cells[uriIndex].value!!
            val action = if (index == 0) longClick() else click()

            // Select test trash items from ui
            onView(withId(R.id.trashList))
                .perform(actionOnHolderItem(withTrashItemUri(Uri.parse(testUri)),action))
        }
    }

    @And("^User restore selected items$")
    fun userRestoreSelectedItems() {
        // Restore all selected items from ui menu
        val context = ApplicationProvider.getApplicationContext<Context>()
        onView(withContentDescription(context.getString(R.string.action_restore)))
            .perform(click())
    }

    @Then("^Restored items should be removed from recycle bin$")
    fun restoredItemsShouldBeRemovedFromRecycleBin(rows: List<TableRow>) {
        // Get all trashed items uris
        val trashedItems = testDao.getAll().blockingFirst().map { it.uri }
        // Extract test data
        val testData = rows.toMutableList()
        val uriIndex = 0

        testData.removeAt(0)
        testData.forEach { row ->
            val testUri = row.cells[uriIndex].value!!

            // Verify restored items were removed from trash
            assertThat(trashedItems.contains(testUri)).isFalse()
        }
    }

    @Then("^Only trashed items should be shown sorted by trashing date in desc order$")
    fun onlyTrashedItemsShouldBeShownSortedByTrashingDateInDescOrder(rows: List<TableRow>) {
        // Extract test data
        val testData = rows.toMutableList()
        val typeIndex = 0
        val uriIndex = 1

        // TODO find a way to sync test thread(main) with AsyncDiffer from ListAdapter
        Thread.sleep(500L)

        testData.removeAt(0)
        testData.forEachIndexed { index, row ->
            val testUri = row.cells[uriIndex].value!!
            val testType = row.cells[typeIndex].value!!

            // Verify test items displayed as expected
            onView(withRecyclerView(R.id.trashList)
                .atPositionOnView(index,getTrashedViewType(testType)))
                .check(matches(isDisplayed()))

            scenario.onFragment {
                val imageView = it.view!!.findViewById<ImageView>(getTrashedViewType(testType))

                verify {
                    when(testType) {
                        "image" -> loadImage(
                            imageView,
                            Uri.parse(testUri)
                            )
                        "video" -> loadThumbnail(
                            imageView,
                            Uri.parse(testUri)
                        )
                    }
                }
            }
        }

        // Verify size of displayed list matches expected list
        scenario.onFragment {
            val rv = it.view!!.findViewById<RecyclerView>(R.id.trashList)
            assertThat(rv.adapter!!.itemCount).isEqualTo(testData.size)
        }
    }

    private fun convertTestDateToTimestamp(testDate: String): Long {
        val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm")
        val d: Date = formatter.parse(testDate) as Date

        return d.time
    }

    @LayoutRes
    fun getTrashedViewType(type: String): Int {
        return when(type) {
            "video" -> R.id.trashedVideoThumb
            "image" -> R.id.trashedPicture
            else -> throw IllegalArgumentException("unrecognized trash item type:$type")
        }
    }
}