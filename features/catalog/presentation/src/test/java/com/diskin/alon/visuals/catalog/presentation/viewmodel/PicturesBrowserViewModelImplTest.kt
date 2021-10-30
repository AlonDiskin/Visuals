package com.diskin.alon.visuals.catalog.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.catalog.presentation.interfaces.PictureRepository
import com.diskin.alon.visuals.catalog.presentation.model.Picture
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
 * [PicturesBrowserViewModelImpl] unit test class.
 */
class PicturesBrowserViewModelImplTest {

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
    private lateinit var viewModel: PicturesBrowserViewModelImpl

    // Mocked collaborator
    private val repository: PictureRepository = mockk()

    // Collaborators stubs
    private val photosSubject: Subject<List<Picture>> = PublishSubject.create()

    @Before
    fun setUp() {
        // Stub mocks
        every{ repository.getAll() } returns  photosSubject

        // Init SUT
        viewModel =
            PicturesBrowserViewModelImpl(
                repository
            )
    }

    @Test
    fun subscribeToPhotosStream_whenCreated() {
        // Given an initialized view model

        // Then view model should have created a subscription to repository photos observable
        verify {repository.getAll() }
        assertThat(photosSubject.hasObservers()).isTrue()
    }

    @Test
    fun freeResources_whenCleared() {
        // Given an initialized view model

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all observable subscriptions should be disposed by view model
        assertThat(photosSubject.hasObservers()).isFalse()
    }

    @Test
    fun updateViewState_whenRepoPhotosErrors() {
        // Given a initialized view model

        // When repository photos subscription emits an error
        val testErrorMessage = "errorMessage"
        photosSubject.onError(Throwable(testErrorMessage))

        // Then view model should update fail event data
        assertThat(viewModel.photosUpdateError.event).isEqualTo(testErrorMessage)
    }

    @Test
    fun updatePicturesState_whenPicturesUpdate() {
        // Given an initialized view model

        // When repository photos are updated
        val testPhotos = arrayListOf(
            Picture(mockk { }),
            Picture(mockk { }),
            Picture(mockk { })
        )

        photosSubject.onNext(testPhotos)

        // Then view model should update photos live data
        assertThat(viewModel.photos.value).isEqualTo(testPhotos)
    }
}