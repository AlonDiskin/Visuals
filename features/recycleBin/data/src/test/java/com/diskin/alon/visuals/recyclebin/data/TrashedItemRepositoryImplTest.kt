package com.diskin.alon.visuals.recyclebin.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.recuclebin.presentation.TrashedFilter
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItem
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemType
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

/**
 * [TrashedItemRepositoryImpl] unit test class. This test uses robolectric to deal with
 * testing [Uri] obj creation and testing on local jvm,that happens during test run.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class TrashedItemRepositoryImplTest(
    private val testFilter: TrashedFilter
) {

    companion object {
        @JvmStatic
        // name argument is optional, it will show up on the test results
        @ParameterizedRobolectricTestRunner.Parameters(name = "Input: {0}")
        // parameters are provided as arrays, allowing more than one parameter
        fun params() = listOf(
            arrayOf(TrashedFilter.ALL),
            arrayOf(TrashedFilter.PICTURE),
            arrayOf(TrashedFilter.VIDEO)
        )
    }

    // System under test
    private lateinit var repository: TrashedItemRepositoryImpl

    // Mocked collaborators
    private val dao = mockk<TrashedItemDao>()
    private val deviceMediaProvider = mockk<DeviceMediaProvider<MediaStoreVisual>>()

    @Before
    fun setUp() {
        // Init SUT
        repository = TrashedItemRepositoryImpl(dao,deviceMediaProvider)
    }

    @Test
    fun createObservableThatEmitTrashedInAddedDescOrder_whenQueriedForAll() {
        // Test case fixture
        val expectedTrashedTypePredicate: (TrashedItemType) -> Boolean = {
            when(testFilter) {
                TrashedFilter.ALL -> true
                TrashedFilter.PICTURE -> it == TrashedItemType.PICTURE
                TrashedFilter.VIDEO -> it == TrashedItemType.VIDEO
            }
        }
        val deletedEntitySlot = slot<TrashedItemEntity>()
        val testDaoSubject: BehaviorSubject<List<TrashedItemEntity>> =
            BehaviorSubject.createDefault(listOf(
                TrashedItemEntity("ur1_1",TrashedEntityType.VIDEO),
                TrashedItemEntity("ur1_2",TrashedEntityType.VIDEO),
                TrashedItemEntity("ur1_3",TrashedEntityType.PICTURE),
                TrashedItemEntity("ur1_4",TrashedEntityType.PICTURE)
            ))

        val testDeviceMediaSubject: BehaviorSubject<List<MediaStoreVisual>> =
            BehaviorSubject.createDefault(listOf(
                MediaStoreVisual(Uri.parse("ur1_1")),
                MediaStoreVisual(Uri.parse("ur1_2")),
                MediaStoreVisual(Uri.parse("ur1_3")),
                MediaStoreVisual(Uri.parse("ur1_4"))
            ))

        every { dao.getAll() } returns testDaoSubject
        every { deviceMediaProvider.getAll() } returns testDeviceMediaSubject
        every { dao.delete(capture(deletedEntitySlot)) } answers {
            val deleted = deletedEntitySlot.captured
            if (testDaoSubject.value!!.contains(deleted)) {
                // update dao subject when item is deleted from dao mock
                val updated = testDaoSubject.value!!.toMutableList()
                updated.remove(deleted)
                testDaoSubject.onNext(updated)
            }

            Single.create<Unit> { emitter ->  emitter.onSuccess(Unit) }.ignoreElement()
        }

        // Given an initialized repository

        // When repository is queried for all trashed items observable
        val actual = repository.getAll(testFilter).test()

        // Then repository should fetch dao observable of all trashed items
        verify { dao.getAll() }

        // And return an observable that emits trashed items,sorted by adding,in descending order
        val expectedItems = listOf(
            TrashedItem(Uri.parse("ur1_4"),TrashedItemType.PICTURE),
            TrashedItem(Uri.parse("ur1_3"),TrashedItemType.PICTURE),
            TrashedItem(Uri.parse("ur1_2"),TrashedItemType.VIDEO),
            TrashedItem(Uri.parse("ur1_1"),TrashedItemType.VIDEO))
            .filter { expectedTrashedTypePredicate(it.type) }

        actual.assertValueAt(0,expectedItems)

        // When trashed item is removed from device
        val expectedDeletedTrashed = TrashedItemEntity(
            "ur1_3",
            TrashedEntityType.PICTURE
        )
        val updatedDeviceList = listOf(
            MediaStoreVisual(Uri.parse("ur1_1")),
            MediaStoreVisual(Uri.parse("ur1_2")),
            MediaStoreVisual(Uri.parse("ur1_4"))
        )

        testDeviceMediaSubject.onNext(updatedDeviceList)

        // Then repository should delete item from dao
        verify { dao.delete(expectedDeletedTrashed) }

        // And trashed observable should emit update without removed item
        val expectedUpdatedItems = listOf(
            TrashedItem(Uri.parse("ur1_4"),TrashedItemType.PICTURE),
            TrashedItem(Uri.parse("ur1_2"),TrashedItemType.VIDEO),
            TrashedItem(Uri.parse("ur1_1"),TrashedItemType.VIDEO))
            .filter { expectedTrashedTypePredicate(it.type) }

        actual.assertValueAt(1,expectedUpdatedItems)
    }
}