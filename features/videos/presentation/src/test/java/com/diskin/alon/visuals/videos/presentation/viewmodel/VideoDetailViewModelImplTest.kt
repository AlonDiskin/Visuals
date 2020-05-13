package com.diskin.alon.visuals.videos.presentation.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoDetailRepository
import com.diskin.alon.visuals.videos.presentation.model.VideoDetail
import com.diskin.alon.visuals.videos.presentation.model.VideoDetailDto
import com.diskin.alon.visuals.videos.presentation.model.VideoInfoError
import com.diskin.alon.visuals.videos.presentation.util.VideoDetailMapper
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
 * [VideoDetailViewModelImpl] unit test class.
 */
class VideoDetailViewModelImplTest {

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
    private lateinit var viewModel: VideoDetailViewModelImpl

    // Mocked collaborators
    private val repository: VideoDetailRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()
    private val mapper: VideoDetailMapper = mockk()

    // Stub data
    private val videoUri: Uri = mockk()
    private val videoDetailDtoSubject: SingleSubject<VideoDetailDto> = SingleSubject.create()

    @Before
    fun setUp() {
        // Stub mocks
        every {
            savedStateHandle.get<Uri>(VideoDetailViewModelImpl.VID_URI)
        } returns videoUri

        every { repository.get(videoUri) } returns videoDetailDtoSubject

        // Create SUT
        viewModel = VideoDetailViewModelImpl(
            repository,
            savedStateHandle,
            mapper
        )
    }

    @Test
    fun fetchVideoInfo_whenCreated() {
        // Test case fixture
        val testVideoDetailDto = VideoDetailDto(
            1.0,
            100L,
            100L,
            "",
            "",
            1,
            1
        )
        val testVideoDetail = VideoDetail(
            "size",
            "added",
            "duration",
            "path",
            "title",
            "resolution"
        )

        every { mapper.mapDetail(testVideoDetailDto) } returns testVideoDetail

        // Given an initialized view model

        // Then view model should fetch video info single from repository
        verify { repository.get(videoUri) }

        // When repository single emits video info
        videoDetailDtoSubject.onSuccess(testVideoDetailDto)

        // Then view model should update its live data state with mapped video data
        verify { mapper.mapDetail(testVideoDetailDto) }
        assertThat(viewModel.videoDetail.value).isEqualTo(testVideoDetail)
    }

    @Test
    fun raiseAnVideoErrorEvent_whenVideoInfoFetchFail() {
        // Given an initialized view model

        // Then view model should fetch video info single from repository
        verify { repository.get(videoUri) }

        // When repository emits an error
        val errorMessage = "error message"
        videoDetailDtoSubject.onError(Throwable(errorMessage))

        // Then view model should update its live data state with expected error info
        val expectedVideoError = VideoInfoError(errorMessage)
        assertThat(viewModel.videoInfoError.event).isEqualTo(expectedVideoError)
    }
}