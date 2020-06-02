package com.diskin.alon.visuals.video.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.data.VideoRepositoryImpl
import com.diskin.alon.visuals.videos.presentation.model.Video
import com.diskin.alon.visuals.videos.presentation.model.VideoDuration
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
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
    private val trashedItemDao: TrashedItemDao = mockk()

    @Before
    fun setUp() {
        // Init SUT
        repository = VideoRepositoryImpl(
            videosProvider,
            trashedItemDao
        )
    }

    @Test
    fun fetchAllPictures_whenPicturesQueried() {
        // Test case fixture
        val testUri = listOf<Uri>(
            mock {  },
            mock {  },
            mock {  },
            mock {  }
        )
        val testTrashedItems = listOf(
            TrashedItemEntity(
                testUri.first().toString(),
                TrashedEntityType.VIDEO
            ),
            TrashedItemEntity(
                testUri.last().toString(),
                TrashedEntityType.VIDEO
            )
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
                testDeviceVideos[2].uri,
                VideoDuration(
                    10,
                    1
                )
            )
        )

        every { videosProvider.getAll() } returns Observable.just(testDeviceVideos)
        every { trashedItemDao.getAll() } returns Observable.just(testTrashedItems)

        // Given an initialized repository, existing public videos on user device,and trashed
        // videos in recycle bin

        // When repository is queried for videos
        val actualPhotos = repository.getAll().blockingFirst()!!

        // Then repository should return an observable that emit all public device videos,
        // that was not moved to app recycle bin,sorted by date in desc order
        assertThat(actualPhotos).isEqualTo(expectedVideos)
    }

    @Test
    fun trashVideos_whenClientTrashes() {
        // Test case fixture
        val testCompletable = mockk<Completable>()

        every { testCompletable.subscribeOn(any()) } returns testCompletable
        every { trashedItemDao.insert(*anyVararg()) } returns testCompletable

        // Given an initialized repository

        // When client ask repository to trash given videos uris
        val testVideosUri = arrayOf<Uri>(mockk(),mockk())
        val expectedInserted = testVideosUri
            .map { TrashedItemEntity(it.toString(), TrashedEntityType.VIDEO) }

        val actual = repository.trash(*testVideosUri)

        // Then repository should insert videos to trash dao
        verify { trashedItemDao.insert(*expectedInserted.toTypedArray()) }

        // And return a completable from dao
        assertThat(actual).isEqualTo(testCompletable)
    }
}