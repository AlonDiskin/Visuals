package com.diskin.alon.visuals.recuclebin.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [TrashedItemsViewModelImpl] unit test class.
 */
class TrashedViewModelImplTest {

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
    private lateinit var viewModel: TrashedItemsViewModelImpl

    // Mocked collaborators
    private val repository: TrashedItemRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    // Collaborator stub data
    private val trashedItemsSubject: Subject<List<TrashedItem>> = PublishSubject.create()
    private val trashedFilter = null

    @Before
    fun setUp() {
        // Stub mocked collaborator
        every { repository.getAll(any()) } returns trashedItemsSubject
        every { savedStateHandle.get<TrashedFilter>(TrashedItemsViewModelImpl.KEY_FILTER) } returns trashedFilter
        every { savedStateHandle.set<TrashedFilter>(TrashedItemsViewModelImpl.KEY_FILTER,any()) } answers {}

        viewModel = TrashedItemsViewModelImpl(repository,savedStateHandle)
    }

    @Test
    fun observeFilteredTrashedItems_whenCreated() {
        // Given an initialized view model

        // Then view model should have fetched trashed items observable from repository
        verify { repository.getAll(TrashedItemsViewModelImpl.DEFAULT_FILTER) }

        // When repository items are emitted
        val testItems = listOf(
            TrashedItem(mockk(), mockk()),
            TrashedItem(mockk(), mockk()),
            TrashedItem(mockk(), mockk())
        )

        trashedItemsSubject.onNext(testItems)

        // Then view model should update its live data state
        assertThat(viewModel.trashedItems.value).isEqualTo(testItems)

        // When user changes filter property
        val testFilter = TrashedFilter.VIDEO
        viewModel.filter = testFilter

        // Then view model should re fetch items observable from repository
        verify { repository.getAll(testFilter) }

        // And discard old prev observable subscription
        val field = TrashedItemsViewModelImpl::class.java.getDeclaredField("disposable")
        field.isAccessible = true
        val disposable = field.get(viewModel) as CompositeDisposable

        assertThat(disposable.size()).isEqualTo(1)
    }

    @Test
    fun cancelSubscriptions_whenCleared() {
        // Given an initialized view model

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all observable subscriptions should be disposed by view model
        val field = TrashedItemsViewModelImpl::class.java.getDeclaredField("disposable")
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
        verify { savedStateHandle.set<TrashedFilter>(TrashedItemsViewModelImpl.KEY_FILTER,viewModel.filter) }
    }
}