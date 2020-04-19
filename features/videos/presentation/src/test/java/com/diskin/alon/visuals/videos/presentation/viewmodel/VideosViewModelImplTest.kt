package com.diskin.alon.visuals.videos.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoRepository
import com.diskin.alon.visuals.videos.presentation.model.Video
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
 * [VideosBrowserViewModelImpl] unit test class.
 */
class VideosViewModelImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // System under test
    private lateinit var viewModel: VideosBrowserViewModelImpl

    // Mocked collaborator
    private val repository: VideoRepository = mockk()

    // Collaborator stub data
    private val videosSubject: Subject<List<Video>> = PublishSubject.create()

    // Lifecycle testing rule
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        // Stub mocks
        every{ repository.getAll() } returns  videosSubject

        // Init SUT
        viewModel =
            VideosBrowserViewModelImpl(
                repository
            )
    }

    @Test
    fun subscribeToVideosStream_whenCreated() {
        // Given an initialized view model

        // Then view model should have created a subscription to repository videos observable
        verify {repository.getAll() }
        assertThat(videosSubject.hasObservers()).isTrue()
    }

    @Test
    fun freeResources_whenCleared() {
        // Given an initialized view model

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all observable subscriptions should be disposed by view model
        assertThat(videosSubject.hasObservers()).isFalse()
    }

    @Test
    fun updateVideosState_whenVideosStreamUpdate() {
        // Given an initialized view model

        // When repository videos are updated
        val testVideos = arrayListOf(
            Video(
                mockk { },
                mockk { }),
            Video(
                mockk { },
                mockk { }),
            Video(
                mockk { },
                mockk { })
        )

        videosSubject.onNext(testVideos)

        // Then view model should update videos live data
        assertThat(viewModel.videos.value).isEqualTo(testVideos)
    }

    @Test
    fun createErrorEvent_whenVideosStreamUpdateFails() {
        // Given a initialized view model

        // When repository videos subscription emits error
        val testErrorMessage = "errorMessage"
        videosSubject.onError(Throwable(testErrorMessage))

        // Then view model should raise an videos update error event
        assertThat(viewModel.videosUpdateFail.event).isEqualTo(testErrorMessage)
    }
}