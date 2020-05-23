package com.diskin.alon.visuals.recuclebin.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
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

    // Collaborator stub data
    private val trashedItemsSubject: Subject<List<TrashedItem>> = PublishSubject.create()

    @Before
    fun setUp() {
        // Stub mocked collaborator
        every { repository.getAll() } returns trashedItemsSubject

        viewModel = TrashedItemsViewModelImpl(repository)
    }

    @Test
    fun fetchTrashedItemsState_whenCreated() {
        // Given an initialized view model

        // Then view model should have fetched trashed items observable from repository
        verify { repository.getAll() }

        // When repository items are emitted
        val testItems = listOf(
            TrashedItem(mockk(), mockk()),
            TrashedItem(mockk(), mockk()),
            TrashedItem(mockk(), mockk())
        )

        trashedItemsSubject.onNext(testItems)

        // Then view model should update its live data state
        assertThat(viewModel.trashedItems.value).isEqualTo(testItems)
    }

    @Test
    fun freeResources_whenCleared() {
        // Given an initialized view model

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all observable subscriptions should be disposed by view model
        assertThat(trashedItemsSubject.hasObservers()).isFalse()
    }
}