package com.diskin.alon.visuals.recuclebin.presentation

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.Event.Status
import com.diskin.alon.visuals.recuclebin.presentation.interfaces.TrashItemRepository
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItem
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashedFilter
import com.diskin.alon.visuals.recuclebin.presentation.viewmodel.TrashBrowserViewModelImpl
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [TrashBrowserViewModelImpl] unit test class.
 */
class TrashBrowserViewModelImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Lifecycle testing rule
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // System under test
    private lateinit var viewModel: TrashBrowserViewModelImpl

    // Mocked collaborators
    private val repository: TrashItemRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    // Collaborator stub data
    private val trashItemsSubject: Subject<List<TrashItem>> = PublishSubject.create()
    private val trashFilter = null

    @Before
    fun setUp() {
        // Stub mocked collaborator
        every { repository.getAll(any()) } returns trashItemsSubject
        every { savedStateHandle.get<TrashedFilter>(TrashBrowserViewModelImpl.KEY_FILTER) } returns trashFilter
        every { savedStateHandle.set<TrashedFilter>(TrashBrowserViewModelImpl.KEY_FILTER,any()) } answers {}

        viewModel =
            TrashBrowserViewModelImpl(
                repository,
                savedStateHandle
            )
    }

    @Test
    fun observeTrashItems_whenCreated() {
        // Given an initialized view model

        // Then view model should have fetched trashed items observable from repository
        verify { repository.getAll(TrashBrowserViewModelImpl.DEFAULT_FILTER) }

        // When repository items are emitted
        val testItems = listOf(
            TrashItem(
                mockk(),
                mockk()
            ),
            TrashItem(
                mockk(),
                mockk()
            ),
            TrashItem(
                mockk(),
                mockk()
            )
        )

        trashItemsSubject.onNext(testItems)

        // Then view model should update its live data state
        assertThat(viewModel.trashItems.value).isEqualTo(testItems)

        // When user changes filter property
        val testFilter = TrashedFilter.VIDEO
        viewModel.filter = testFilter

        // Then view model should re fetch items observable from repository
        verify { repository.getAll(testFilter) }
    }

    @Test
    fun cancelSubscriptions_whenCleared() {
        // Given an initialized view model

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all observable subscriptions should be disposed by view model
        val field = TrashBrowserViewModelImpl::class.java.getDeclaredField("disposable")
        field.isAccessible = true
        val disposable = field.get(viewModel) as Disposable

        assertThat(disposable.isDisposed).isTrue()
    }

    @Test
    fun saveFilterTypeState_whenCleared() {
        // Given an initialized view model

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then view model should save filter type state in SavedStateHandle
        verify { savedStateHandle.set<TrashedFilter>(TrashBrowserViewModelImpl.KEY_FILTER,viewModel.filter) }
    }

    @Test
    fun restoreTrashItems() {
        // Test case fixture
        val testRestoreSubject = CompletableSubject.create()
        every { repository.restore(any()) } returns testRestoreSubject

        // Given an initialized view model

        // When view ask to restore a list of given trash items
        val testItems =  listOf<Uri>(mockk(),mockk(),mockk())
        viewModel.restore(testItems)

        // Then view model should ask repository to restore the given items
        verify { repository.restore(testItems) }
    }

    @Test
    fun updateItemsRestoreEventSuccess_whenItemsRestored() {
        // Test case fixture
        val testRestoreSubject = CompletableSubject.create()
        every { repository.restore(any()) } returns testRestoreSubject

        // Given an initialized view model

        // When view ask to restore a list of given trash items
        val testItems =  listOf<Uri>(mockk(),mockk(),mockk())
        viewModel.restore(testItems)

        // And repository complete restoration
        testRestoreSubject.onComplete()

        // Then view model should update restore event as a success
        assertThat(viewModel.restoreEvent.event).isEqualTo(Event(Status.SUCCESS))
    }

    @Test
    fun updateItemsRestoreEventFailure_whenItemsRestoreFails() {
        // Test case fixture
        val testRestoreSubject = CompletableSubject.create()
        every { repository.restore(any()) } returns testRestoreSubject

        // Given an initialized view model

        // When view ask to restore a list of given trash items
        val testItems =  listOf<Uri>(mockk(),mockk(),mockk())
        viewModel.restore(testItems)

        // And repository fail to restore items
        testRestoreSubject.onError(Throwable())

        // Then view model should update restore event as a failure
        assertThat(viewModel.restoreEvent.event).isEqualTo(Event(Status.FAILURE))
    }

    @Test
    fun restoreAllItems_whenInvoked() {
        // Test case fixture
        val testRestoreSubject = CompletableSubject.create()
        every { repository.restoreAll() } returns testRestoreSubject

        // Given an initialized view model

        // When view model is asked by view to restore all trash items
        viewModel.restoreAll()

        // Then view model should ask repository to restore all its otems
        verify { repository.restoreAll() }
    }

    @Test
    fun updateRestoreAllEventFailure_whenRestoreAllFails() {
        // Test case fixture
        val testRestoreSubject = CompletableSubject.create()
        every { repository.restoreAll() } returns testRestoreSubject

        // Given an initialized view model

        // When view model is asked by view to restore all trash items
        viewModel.restoreAll()

        // And repository fail to restore all items
        testRestoreSubject.onError(Throwable())

        // Then view model should update restore all event as a failure
        assertThat(viewModel.restoreAllEvent.event).isEqualTo(Event(Status.FAILURE))
    }
}