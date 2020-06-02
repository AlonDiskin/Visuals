package com.diskin.alon.visuals.recyclebin.featuretest

import android.net.Uri
import android.os.Looper
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItem
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemType
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedPictureBinding
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedVideoBinding
import com.diskin.alon.visuals.recuclebin.presentation.loadImage
import com.diskin.alon.visuals.recuclebin.presentation.loadThumbnail
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import io.mockk.verify
import org.robolectric.Shadows

/**
 * Step definitions for the 'Trashed items state are displayed to user' scenario.
 */
class TrashedStateShownItemsSteps(
    private val testDao: TrashedItemDao,
    mockedMediaProvider: DeviceMediaProvider<MediaStoreVisual>
) : ListTrashedItemsBackgroundSteps(testDao, mockedMediaProvider) {

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

    @Then("^Only trashed items should be shown sorted by trashing date in desc order$")
    override fun onlyTrashedItemsShouldBeShownSortedByTrashingDateInDescOrder(rows: List<TableRow>) {
        super.onlyTrashedItemsShouldBeShownSortedByTrashingDateInDescOrder(rows)
    }

    @When("^Items are removed from recycle bin$")
    fun itemsAreRemovedFromRecycleBin(rows: List<TableRow>) {
        val testRemovedItems = mutableListOf<TrashedItemEntity>()
        val testData = rows.toMutableList()
        val typeIndex = 0
        val uriIndex = 1

        testData.removeAt(0)
        testData.forEach { row ->
            val testUri = row.cells[uriIndex].value!!
            val testType = row.cells[typeIndex].value!!

            testRemovedItems.add(
                TrashedItemEntity(
                    testUri,
                    when(testType) {
                        "image" -> TrashedEntityType.PICTURE
                        else -> TrashedEntityType.VIDEO
                    }
                )
            )
        }

        testDao.delete(*testRemovedItems.toTypedArray()).blockingAwait()
        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()
    }

    @And("^Items Are added$")
    fun itemsAreAdded(rows: List<TableRow>) {
        val testAddedItems = mutableListOf<TrashedItemEntity>()
        val testData = rows.toMutableList()
        val typeIndex = 0
        val uriIndex = 1

        testData.removeAt(0)
        testData.forEach { row ->
            val testUri = row.cells[uriIndex].value!!
            val testType = row.cells[typeIndex].value!!

            testAddedItems.add(
                TrashedItemEntity(
                    testUri,
                    when(testType) {
                        "image" -> TrashedEntityType.PICTURE
                        else -> TrashedEntityType.VIDEO
                    }
                )
            )
        }

        testDao.insert(*testAddedItems.toTypedArray()).blockingAwait()
        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()
    }

    @Then("^Updated trash items list should be displayed$")
    fun updatedTrashItemsListShouldBeDisplayed(rows: List<TableRow>) {
        // Extract test items
        val expectedTrashedItems = mutableListOf<TrashedItem>()
        val testTrashedItems = rows.toMutableList()
        val typeIndex = 0
        val uriIndex = 1

        testTrashedItems.removeAt(0)
        testTrashedItems.forEach { row ->
            val testUri = row.cells[uriIndex].value!!
            val testType = row.cells[typeIndex].value!!

            expectedTrashedItems.add(
                TrashedItem(
                    Uri.parse(testUri),
                    when(testType) {
                        "image" -> TrashedItemType.PICTURE
                        else -> TrashedItemType.VIDEO
                    }
                )
            )
        }

        // TODO find a way to sync test thread(main) with AsyncDiffer from ListAdapter
        Thread.sleep(500L)

        // Verify expected trashed items displayed in fragment layout
        expectedTrashedItems.forEachIndexed { index, item ->
            onView(
                RecyclerViewMatcher.withRecyclerView(R.id.trashedList)
                    .atPositionOnView(index, getTrashedViewType(item))
            )
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

            // Verify image/video thumbnail is loaded for display
            verify {
                when(item.type) {
                    TrashedItemType.PICTURE -> loadImage(any(),item.uri)
                    TrashedItemType.VIDEO -> loadThumbnail(any(),item.uri)
                }
            }

            scenario.onFragment {
                // Verify  expected uris has been bounded to layout views at expected positions
                val rv = it.view!!.findViewById<RecyclerView>(R.id.trashedList)
                val binding = DataBindingUtil.getBinding<ViewDataBinding>(
                    rv[index]
                )

                val boundedItem = if (binding is TrashedVideoBinding) {
                    binding.trashedItem!!
                } else  {
                    (binding as TrashedPictureBinding).trashedItem!!
                }

                assertThat(boundedItem.uri).isEqualTo(item.uri)

                // Verify size of displayed list matches expected list
                assertThat(rv.adapter!!.itemCount).isEqualTo(expectedTrashedItems.size)
            }
        }
    }
}