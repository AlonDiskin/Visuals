package com.diskin.alon.visuals.recyclebin.featuretest

import android.net.Uri
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
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
import com.diskin.alon.visuals.recyclebin.featuretest.RecyclerViewMatcher.withRecyclerView
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import io.mockk.verify

/**
 * Step definitions for the 'User delete trashed from device outside app' scenario.
 */
class TrashedDeletedOutsideAppSteps(
    private val testDao: TrashedItemDao,
    mockedMediaProvider: DeviceMediaProvider<MediaStoreVisual>
) :ListTrashedItemsBackgroundSteps(testDao,mockedMediaProvider) {

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

    @When("^User deletes trashed item from device not via app$")
    fun userDeletesTrashedItemFromDeviceNotViaApp(rows: List<TableRow>) {
        // Extract test data
        val testMediaItems = testDeviceMediaSubject.value!!.toMutableList()
        val testMediaToDelete = mutableListOf<MediaStoreVisual>()
        val testData = rows.toMutableList()
        val uriIndex = 1

        testData.removeAt(0)
        testData.forEach { row ->
            val testUri = row.cells[uriIndex].value!!
            testMediaToDelete.add(
                MediaStoreVisual(
                    Uri.parse(testUri)
                )
            )
        }

        // Update mocked device media provider with new list without deleted items
        testMediaItems.removeAll(testMediaToDelete)
        testDeviceMediaSubject.onNext(testMediaItems)
    }

    @Then("^Trashed items in recycle bin should be updated$")
    fun trashedItemsInRecycleBinShouldBeUpdated(rows: List<TableRow>) {
        // Extract test data
        val expectedDaoItems = mutableListOf<TrashedItemEntity>()
        val testData = rows.toMutableList()
        val typeIndex = 0
        val uriIndex = 1

        testData.removeAt(0)

        testData.forEach { row ->
            val testUri = row.cells[uriIndex].value!!
            val testType = row.cells[typeIndex].value!!

            expectedDaoItems.add(
                TrashedItemEntity(
                    testUri,
                    when(testType) {
                        "image" -> TrashedEntityType.PICTURE
                        else -> TrashedEntityType.VIDEO
                    }
                )
            )
        }

        // Verify test data is equal to current dao items
        assertThat(testDao.getAll().blockingFirst()).isEqualTo(expectedDaoItems)
    }

    @And("^Updated trashed state should be displayed sorted by trashing date in desc order$")
    fun updatedTrashedStateShouldBeDisplayedSortedByTrashingDateInDescOrder(rows: List<TableRow>) {
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
                withRecyclerView(R.id.trashedList)
                    .atPositionOnView(index, getTrashedViewType(item))
            )
                .check(matches(isDisplayed()))

            verify {
                when(item.type) {
                    TrashedItemType.PICTURE -> loadImage(any(),item.uri)
                    TrashedItemType.VIDEO -> loadThumbnail(any(),item.uri)
                }
            }

            scenario.onFragment {
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
            }
        }

        // Verify ui display expected size of items
        scenario.onFragment {
            val rv = it.view!!.findViewById<RecyclerView>(R.id.trashedList)
            assertThat(rv.adapter!!.itemCount).isEqualTo(expectedTrashedItems.size)
        }
    }
}