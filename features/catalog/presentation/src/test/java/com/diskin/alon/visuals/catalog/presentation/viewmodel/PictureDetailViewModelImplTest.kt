package com.diskin.alon.visuals.catalog.presentation.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.visuals.catalog.presentation.interfaces.PictureDetailRepository
import com.diskin.alon.visuals.catalog.presentation.model.PictureDetail
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [PictureDetailViewModelImpl] unit test class.
 */
class PictureDetailViewModelImplTest {

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
    private lateinit var viewModel: PictureDetailViewModelImpl

    // Mocked collaborators
    private val repository: PictureDetailRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    // Collaborators stubs
    private val uri = mockk<Uri>()
    private val pictureDetailSubject = SingleSubject.create<PictureDetail>()

    @Before
    fun setUp() {
        // Stub mocks
        every {
            savedStateHandle.get<Uri>(PictureDetailViewModelImpl.KEY_PIC_URI)
        } returns uri

        every { repository.get(uri) } returns pictureDetailSubject

        viewModel = PictureDetailViewModelImpl(repository, savedStateHandle)
    }

    @Test
    fun loadPictureDetail_whenCreated() {
        // Given an initialized view model instance

        // Then view model should have initiated picture detail loading from repository
        val testPictureDetail = PictureDetail(
            12.3,
            mockk(),
            "path",
            "title",
            1000,
            980
        )

        verify { repository.get(uri) }

        // When picture is loaded
        pictureDetailSubject.onSuccess(testPictureDetail)

        // Then view model should update its picture detail state
        assertThat(viewModel.pictureDetail.value).isEqualTo(testPictureDetail)
    }

    @Test
    fun dispatchErrorEvent_whenPictureLoadingFail() {
        // Given an initialized view model instance

        // Then view model should have initiated picture detail loading from repository
        verify { repository.get(uri) }

        // When picture loading fails
        pictureDetailSubject.onError(Throwable())

        // Then view model should update its error event data
        assertThat(viewModel.pictureError.event).isNotNull()
    }
}