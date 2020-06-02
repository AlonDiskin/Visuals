package com.diskin.alon.visuals.video.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.data.VideoDetailRepositoryImpl
import com.diskin.alon.visuals.videos.presentation.model.VideoDetailDto
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [VideoDetailRepositoryImpl] unit test class.
 */
class VideoDetailRepositoryImplTest {

    // System under test
    private lateinit var repository: VideoDetailRepositoryImpl

    // Mocked collaborator
    private val videosProvider: DeviceMediaProvider<MediaStoreVideo> = mockk()

    @Before
    fun setUp() {
        // Create SUT
        repository = VideoDetailRepositoryImpl(videosProvider)
    }

    @Test
    fun createVideoDetailSingleObservable_whenQueried() {
        // Test case fixture
        val testUri = mockk<Uri>()
        val testMediaStoreVideo = MediaStoreVideo(
            testUri,
            100L,
            100L,
            3500000L,
            "title",
            "path",
            1200L,
            1000L
        )
        val testDeviceVideos = listOf<MediaStoreVideo>(
            MediaStoreVideo(mockk(), 10L, 150L, 6500000L, "", "", 1000L, 900L),
            testMediaStoreVideo
        )
        val expectedVideoDetailDto = VideoDetailDto(
            3.5,
            100L,
            100L,
            "path",
            "title",
            1200,
            1000
        )

        every { videosProvider.getAll() } returns Observable.just(testDeviceVideos)

        // Given an initialized repository

        // When repository is queried for video detail
        val actual = repository.get(testUri)

        // Then repository should create a single observable from videos provider
        // that emits expected video detail
        assertThat(actual.blockingGet()).isEqualTo(expectedVideoDetailDto)
    }
}