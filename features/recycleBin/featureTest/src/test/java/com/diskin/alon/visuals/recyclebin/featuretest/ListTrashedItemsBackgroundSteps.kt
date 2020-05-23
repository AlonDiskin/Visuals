package com.diskin.alon.visuals.recyclebin.featuretest

import android.net.Uri
import android.os.Looper
import androidx.annotation.LayoutRes
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.recuclebin.presentation.*
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import com.diskin.alon.visuals.recyclebin.featuretest.RecyclerViewMatcher.withRecyclerView
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import gherkin.ast.TableRow
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verifyOrder
import io.reactivex.subjects.BehaviorSubject
import org.robolectric.Shadows
import java.text.SimpleDateFormat
import java.util.*

/**
 * List trashed items feature file background steps.
 */
open class ListTrashedItemsBackgroundSteps(
    private val testDao: TrashedItemDao,
    private val mockedMediaProvider: DeviceMediaProvider<MediaStoreVisual>
): GreenCoffeeSteps() {

    protected lateinit var scenario: FragmentScenario<TrashedItemsFragment>
    protected val testDeviceMediaSubject: BehaviorSubject<List<MediaStoreVisual>> =
        BehaviorSubject.create()

    open fun userHasPublicMediaOnDevice(rows: List<TableRow>) {
        val testDeviceMediaItems = mutableListOf<MediaStoreVisual>()
        val testData = rows.toMutableList()
        val uriIndex = 1

        testData.removeAt(0)
        testData.forEach { row ->
            val testUri = row.cells[uriIndex].value!!

            testDeviceMediaItems.add(
                MediaStoreVisual(
                    Uri.parse(testUri)
                )
            )
        }

        // Add test data to mocked device media provider
        testDeviceMediaSubject.onNext(testDeviceMediaItems)

        // Stub mocked device provider
        every { mockedMediaProvider.getAll() } returns testDeviceMediaSubject
    }

    open fun userHesMediaItemsInAppRecycleBin(rows: List<TableRow>) {
        // Extract test data and populate test database via dao by trashed date in ascending order
        // as would be when user tadd item to trashed dao by default
        //val testDeviceMediaItems = mutableListOf<MediaStoreVisual>()
        val testTrashedItems = mutableListOf<TrashedItemEntity>()
        val testData = rows.toMutableList()
        val typeIndex = 0
        val uriIndex = 1
        val addedIndex = 2

        testData.removeAt(0)
        testData.sortWith(kotlin.Comparator { o1, o2 ->
            val first = convertTestDateToTimestamp(o1.cells[addedIndex].value!!)
            val second = convertTestDateToTimestamp(o2.cells[addedIndex].value!!)

            first.compareTo(second)
        })

        testData.forEach { row ->
            val testUri = row.cells[uriIndex].value!!
            val testType = row.cells[typeIndex].value!!

            testTrashedItems.add(
                TrashedItemEntity(
                    testUri,
                    when(testType) {
                        "image" -> TrashedEntityType.PICTURE
                        else -> TrashedEntityType.VIDEO
                    }
                )
            )
        }

        // Add test data to test db
        testDao.insert(*testTrashedItems.toTypedArray()).blockingAwait()
    }

    open fun userOpensRecycleBinScreen() {
        // Mock out media ui loading for verification purposes
        mockkStatic("com.diskin.alon.visuals.recuclebin.presentation.BindingAdaptersKt")

        // Launch trashed items fragment
        scenario = FragmentScenario.launchInContainer(TrashedItemsFragment::class.java)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    open fun onlyTrashedItemsShouldBeShownSortedByTrashingDateInDescOrder(rows: List<TableRow>) {
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

        // Verify expected trashed items displayed in fragment layout
        expectedTrashedItems.forEachIndexed { index, item ->
            onView(withRecyclerView(R.id.trashedList)
                .atPositionOnView(index,getTrashedViewType(item)))
                .check(matches(isDisplayed()))
        }

        verifyOrder {
            expectedTrashedItems.forEach { item ->
                when(item.type) {
                    TrashedItemType.PICTURE -> loadImage(any(),item.uri)
                    TrashedItemType.VIDEO -> loadThumbnail(any(),item.uri)
                }
            }
        }

        // Verify size of displayed list matches expected list
        scenario.onFragment {
            val rv = it.view!!.findViewById<RecyclerView>(R.id.trashedList)
            assertThat(rv.adapter!!.itemCount).isEqualTo(expectedTrashedItems.size)
        }
    }

    private fun convertTestDateToTimestamp(testDate: String): Long {
        val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm")
        val d: Date = formatter.parse(testDate) as Date

        return d.time
    }

    @LayoutRes
    fun getTrashedViewType(item: TrashedItem): Int {
        return when(item.type) {
            TrashedItemType.VIDEO -> R.id.trashedVideoThumb
            TrashedItemType.PICTURE -> R.id.trashedPicture
        }
    }
}