package com.diskin.alon.visuals.photos.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * [PicturesViewModelImpl] unit test class.
 */
class PicturesViewModelImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // System under test
    private lateinit var viewModel: PicturesViewModelImpl

    // Mocked collaborator
    @Mock
    lateinit var repository: PictureRepository

    // Collaborators stubs
    private val photosSubject: Subject<List<Picture>> = PublishSubject.create()

    // Lifecycle testing rule
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        // Init mocks
        MockitoAnnotations.initMocks(this)

        // Stub mocks
        whenever(repository.getAll()).doReturn(photosSubject)

        // Init SUT
        viewModel = PicturesViewModelImpl((repository))
    }

    @Test
    fun subscribeToRepoPhotos_whenCreated() {
        // Given an initialized view model

        // Then view model should have created a subscription to repository photos observable
        verify(repository).getAll()
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
    fun updatePhotosState_whenRepoPhotosUpdate() {
        // Given an initialized view model

        // When repository photos are updated
        val testPhotos = arrayListOf(
            Picture(mock {  }),
            Picture(mock {  }),
            Picture(mock {  })
        )

        photosSubject.onNext(testPhotos)

        // Then view model should update photos live data
        assertThat(viewModel.photos.value).isEqualTo(testPhotos)
    }
}