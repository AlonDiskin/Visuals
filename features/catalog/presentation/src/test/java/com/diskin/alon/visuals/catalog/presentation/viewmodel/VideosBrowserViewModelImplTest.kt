package com.diskin.alon.visuals.catalog.presentation.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.Event.Status
import com.diskin.alon.visuals.catalog.presentation.interfaces.VideoRepository
import com.diskin.alon.visuals.catalog.presentation.model.Video
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import io.reactivex.subjects.Subject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [VideosBrowserViewModelImpl] unit test class.
 */
class VideosBrowserViewModelImplTest {

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
    private lateinit var viewModel: VideosBrowserViewModelImpl

    // Mocked collaborator
    private val repository: VideoRepository = mockk()

    // Collaborator stub data
    private val videosSubject: Subject<List<Video>> = PublishSubject.create()
    private val videosTrashSubject = SingleSubject.create<List<Uri>>()
    private val videosRemoveFromTrashSubject = CompletableSubject.create()

    @Before
    fun setUp() {
        // Stub mocks
        every { repository.getAll() } returns  videosSubject
        every { repository.trash(any()) } returns videosTrashSubject
        every { repository.restoreFromTrash(any()) } returns videosRemoveFromTrashSubject

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
    fun cancelSubscriptions_whenCleared() {
        // Given an initialized view model

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all observable subscriptions should be disposed by view model
        val field = VideosBrowserViewModelImpl::class.java.getDeclaredField("compositeDisposable")
        field.isAccessible = true
        val disposable = field.get(viewModel) as Disposable

        assertThat(disposable.isDisposed).isTrue()
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

    @Test
    fun addVideosToRecycleBin_whenViewTrashVideos() {
        // Given an initialized view model

        // When view model trash videos via repository
        val testVideosUri = listOf<Uri>(
            mockk(),
            mockk(),
            mockk()
        )

        viewModel.trashVideos(testVideosUri)

        // Then view model should ask repository to trash videos
        verify { repository.trash(testVideosUri) }

        // And subscribe to repository completable

        assertThat(this.videosTrashSubject.hasObservers()).isTrue()
    }

    @Test
    fun updateVideosTrashEvent_WhenTrashingCompletes() {
        // Given an initialized view model

        // When repository completes videos trashing with success
        val testVideos = listOf<Uri>(mockk(), mockk())
        viewModel.trashVideos(testVideos)
        videosTrashSubject.onSuccess(testVideos)

        // Then view model should update trashing event as successful event
        assertThat(viewModel.videosTrashedEvent.event).isEqualTo(Event(Status.SUCCESS))
    }

    @Test
    fun updateVideosTrashEvent_WhenTrashingFails() {
        // Given an initialized view model

        // When repository completes videos trashing with error
        viewModel.trashVideos(listOf(mockk(), mockk()))
        videosTrashSubject.onError(Throwable())

        // Then view model should update trashing event as successful event
        assertThat(viewModel.videosTrashedEvent.event).isEqualTo(Event(Status.FAILURE))
    }

    @Test
    fun cacheLastTrashedVideosUri_whenTrashCompletes() {
        // Given an initialized view model

        // When view model trash videos
        val testVideosUri = listOf<Uri>(mockk(), mockk())
        viewModel.trashVideos(testVideosUri)

        // And repository completed successfully trashing
        videosTrashSubject.onSuccess(testVideosUri)

        // Then view model should cache result in local memory
        val field = VideosBrowserViewModelImpl::class.java.getDeclaredField("lastTrashedCache")
        field.isAccessible = true
        val actualCache = field.get(viewModel) as List<*>

        assertThat(actualCache).isEqualTo(testVideosUri)
    }

    @Test
    fun removeLastTrashedFromRecycleBin_whenViewUndoTrashing() {
        // Test case fixture
        val testCacheUris = listOf<Uri>(mockk(), mockk())
        val field = VideosBrowserViewModelImpl::class.java.getDeclaredField("lastTrashedCache")
        field.isAccessible = true

        field.set(viewModel,testCacheUris)

        // Given an initialized view model with cached videos uris fro recent trashing

        // When view ask view model to undo last videos trash
        viewModel.undoLastTrash()

        // Then view model should ask repository to remove from trash the cached uris
        repository.restoreFromTrash(testCacheUris)
    }
}