package com.diskin.alon.visuals.recyclebin.featuretest.restoretrashed

import android.net.Uri
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.recuclebin.presentation.R
import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashBrowserFragment
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import gherkin.ast.TableRow
import io.mockk.every
import io.mockk.mockkStatic
import io.reactivex.subjects.BehaviorSubject
import org.robolectric.Shadows
import java.text.SimpleDateFormat
import java.util.*

abstract class RestoreTrashedStepsBackground(
    private val testDao: TrashedItemDao,
    private val mockedMediaProvider: DeviceMediaProvider<MediaStoreVisual>
) : GreenCoffeeSteps() {

    protected lateinit var scenario: FragmentScenario<TrashBrowserFragment>
    private val testDeviceMediaSubject: BehaviorSubject<List<MediaStoreVisual>> =
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
        mockkStatic("com.diskin.alon.visuals.recuclebin.presentation.util.BindingAdaptersKt")

        // Launch trashed items fragment
        scenario = FragmentScenario.launchInContainer(
            TrashBrowserFragment::class.java,
            null,
            R.style.AppTheme,
            null)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    private fun convertTestDateToTimestamp(testDate: String): Long {
        val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm")
        val d: Date = formatter.parse(testDate) as Date

        return d.time
    }
}