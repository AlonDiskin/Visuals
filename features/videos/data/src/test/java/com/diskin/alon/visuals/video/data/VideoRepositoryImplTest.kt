package com.diskin.alon.visuals.video.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.data.VideoRepositoryImpl
import com.diskin.alon.visuals.videos.presentation.model.Video
import com.diskin.alon.visuals.videos.presentation.model.VideoDuration
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.mock
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * [VideoRepositoryImpl] unit test class.
 */
class VideoRepositoryImplTest {

    // System under test
    private lateinit var repository: VideoRepositoryImpl

    // Mocked collaborator
    private val videosProvider: DeviceMediaProvider<MediaStoreVideo> = mockk()

    @Before
    fun setUp() {
        // Init SUT
        repository = VideoRepositoryImpl(
            videosProvider
        )
    }

    @Test
    fun returnSortedDescendingPictures_whenPicturesQueried() {
        // Test case fixture
        val testUri = listOf<Uri>(
            mock {  },
            mock {  },
            mock {  },
            mock {  }
        )
        val testDeviceVideos = listOf(
            MediaStoreVideo(
                testUri[0],
                10L,
                TimeUnit.MINUTES.toMillis(2) + TimeUnit.SECONDS.toMillis(10)
            ),
            MediaStoreVideo(
                testUri[1],
                900L,
                TimeUnit.SECONDS.toMillis(40)
            ),
            MediaStoreVideo(
                testUri[2],
                30L,
                TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(10)
            ),
            MediaStoreVideo(
                testUri[3],
                140L,
                TimeUnit.SECONDS.toMillis(37)
            )
        )
        val expectedVideos = listOf(
            Video(
                testDeviceVideos[1].uri,
                VideoDuration(
                    40,
                    0
                )
            ),
            Video(
                testDeviceVideos[3].uri,
                VideoDuration(
                    37,
                    0
                )
            ),
            Video(
                testDeviceVideos[2].uri,
                VideoDuration(
                    10,
                    1
                )
            ),
            Video(
                testDeviceVideos[0].uri,
                VideoDuration(
                    10,
                    2
                )
            )
        )

        every { videosProvider.getAll() } returns Observable.just(testDeviceVideos)

        // Given an initialized repository

        // When repository is queried for videos
        val actualPhotos = repository.getAll().blockingFirst()!!

        // Then repository should fetch videos observable from provider, and return
        // a mapped observable, that emits pictures sorted by date in decs order
        Truth.assertThat(actualPhotos).isEqualTo(expectedVideos)
    }
}