package com.diskin.alon.visuals.video.data

import android.content.ContentResolver
import android.database.MatrixCursor
import android.net.Uri
import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.common.data.RxContentProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.data.MediaStoreVideoProvider
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [MediaStoreVideoProvider] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class MediaStoreVideoProviderTest {

    // System under test
    private lateinit var provider: MediaStoreVideoProvider

    // Mocked collaborators
    private val contentResolver: ContentResolver = mockk()

    @Before
    fun setUp() {
        // Init SUT
        provider = MediaStoreVideoProvider(
            contentResolver
        )
    }

    @Test
    fun createMediaStoreVideosContentObservable_whenVideosQueried() {
        // Test case fixture
        mockkObject(RxContentProvider)

        // Given an initialized provider

        // When provider is queried for media store videos
        provider.getAll()

        // Then provider should return videos observable created by RxContentProvider, passing
        // it the expected arguments
        verify { RxContentProvider.create(
            any<() -> List<MediaStoreVideo>>(),
            contentResolver,
            MediaStoreVideoProvider.VIDEOS_PROVIDER_URI) }
    }

    @Test
    fun fetchVideosFromDevice_whenContentObservableInvokesFetchingFunction() {
        // Test case fixture
        val fetchFuncSlot = slot<() -> List<MediaStoreVideo>>()
        val expectedColumns = arrayOf(
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATE_ADDED,
            MediaStore.Video.VideoColumns.DURATION,
            MediaStore.Video.VideoColumns.SIZE,
            MediaStore.Video.VideoColumns.TITLE,
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.VideoColumns.WIDTH,
            MediaStore.Video.VideoColumns.HEIGHT
        )
        val expectedVideos = listOf(
            MediaStoreVideo(
                Uri.parse(
                    MediaStoreVideoProvider.VIDEOS_PROVIDER_URI.toString().plus("/1")
                ),
                10L * 1000L,
                12L,
                10000L,
                "title 1",
                "path 1",
                1000L,
                800L
            ),
            MediaStoreVideo(
                Uri.parse(
                    MediaStoreVideoProvider.VIDEOS_PROVIDER_URI.toString().plus("/2")
                ),
                990L * 1000L,
                1050L,
                15000L,
                "title 2",
                "path 2",
                1500L,
                860L
            ),
            MediaStoreVideo(
                Uri.parse(
                    MediaStoreVideoProvider.VIDEOS_PROVIDER_URI.toString().plus("/3")
                ),
                30567L * 1000L,
                120L,
                17000L,
                "title 3",
                "path 3",
                1200L,
                900L
            )
        )
        val cursor = MatrixCursor(expectedColumns)

        cursor.addRow(arrayOf(1,10L,12L,10000L,"title 1","path 1",1000L,800L))
        cursor.addRow(arrayOf(2,990L,1050L,15000L,"title 2","path 2",1500L,860L))
        cursor.addRow(arrayOf(3,30567L,120L,17000L,"title 3","path 3",1200L,900L))

        mockkObject(RxContentProvider)

        every { contentResolver.query(
            MediaStoreVideoProvider.VIDEOS_PROVIDER_URI,
            expectedColumns,
            null,
            null,
            null
        )
        } returns cursor

        // Given an initialized provider

        // When provider is queried for videos
        provider.getAll()

        // And resulted observable invokes provider videos content fetching function
        verify { RxContentProvider.create(
            capture(fetchFuncSlot),
            contentResolver,
            MediaStoreVideoProvider.VIDEOS_PROVIDER_URI) }

        val actualPictures = fetchFuncSlot.captured.invoke()

        // Then providers content resolver should be queried with expected argument
        verify { contentResolver.query(
            MediaStoreVideoProvider.VIDEOS_PROVIDER_URI,
            expectedColumns,
            null,
            null,
            null
        )
        }

        // And provider should fetch media store videos values, and map them to pictures
        assertThat(actualPictures).isEqualTo(expectedVideos)
    }
}