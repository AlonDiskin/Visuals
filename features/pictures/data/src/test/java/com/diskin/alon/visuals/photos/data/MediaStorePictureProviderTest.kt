package com.diskin.alon.visuals.photos.data

import android.content.ContentResolver
import android.database.MatrixCursor
import android.icu.util.Calendar
import android.net.Uri
import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.common.data.RxContentProvider
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [MediaStorePictureProvider] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class MediaStorePictureProviderTest {

    // System under test
    private lateinit var provider: MediaStorePictureProvider

    // Mocked collaborators
    private val contentResolver: ContentResolver = mockk()

    @Before
    fun setUp() {
        // Init SUT
        provider = MediaStorePictureProvider(contentResolver)
    }

    @Test
    fun createMediaStoreImagesContentObservable_whenPicturesQueried() {
        // Test case fixture
        mockkObject(RxContentProvider)

        // Given an initialized provider

        // When provider is queried for media store pictures
        provider.getAll()

        // Then provider should return pictures observable created by RxContentProvider, passing
        // it the expected arguments
        verify { RxContentProvider.create(
            any<() -> List<MediaStorePicture>>(),
            contentResolver,
            MediaStorePictureProvider.PICTURES_PROVIDER_URI) }
    }

    @Test
    fun fetchPicturesFromDevice_whenContentObservableInvokesFetchingFunction() {
        // Test case fixture
        val fetchFuncSlot = slot<() -> List<MediaStorePicture>>()
        val added = Calendar.getInstance().timeInMillis / 1000L // convert to seconds,as media store value
        val size = 10000L
        val title = "title"
        val path = "path"
        val width = 200L
        val height = 300L
        val expectedColumns = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns.SIZE,
            MediaStore.Images.ImageColumns.TITLE,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT
        )
        val expectedPictures = listOf(
            MediaStorePicture(Uri.parse(
                MediaStorePictureProvider.PICTURES_PROVIDER_URI.toString().plus("/1")),
                added * 1000L, size, title, path, width, height),
            MediaStorePicture(
                Uri.parse(MediaStorePictureProvider.PICTURES_PROVIDER_URI.toString().plus("/2")),
                added * 1000L, size, title, path, width, height),
            MediaStorePicture(
                Uri.parse(MediaStorePictureProvider.PICTURES_PROVIDER_URI.toString().plus("/3")),
                added * 1000L, size, title, path, width, height),
            MediaStorePicture(
                Uri.parse(MediaStorePictureProvider.PICTURES_PROVIDER_URI.toString().plus("/4")),
                added * 1000L, size, title, path, width, height)
        )
        val cursor = MatrixCursor(expectedColumns)

        cursor.addRow(arrayOf(1,added,size,title,path,width,height))
        cursor.addRow(arrayOf(2,added,size,title,path,width,height))
        cursor.addRow(arrayOf(3,added,size,title,path,width,height))
        cursor.addRow(arrayOf(4,added,size,title,path,width,height))

        mockkObject(RxContentProvider)

        every { contentResolver.query(
            MediaStorePictureProvider.PICTURES_PROVIDER_URI,
            expectedColumns,
            null,
            null,
            null
        )
        } returns cursor

        // Given an initialized provider

        // When provider is queried for pictures
        provider.getAll()

        // And resulted observable invokes provider pictures content fetching function
        verify { RxContentProvider.create(
            capture(fetchFuncSlot),
            contentResolver,
            MediaStorePictureProvider.PICTURES_PROVIDER_URI) }

        val actualPictures = fetchFuncSlot.captured.invoke()

        // Then providers content resolver should be queried with expected argument
        verify { contentResolver.query(
            MediaStorePictureProvider.PICTURES_PROVIDER_URI,
            expectedColumns,
            null,
            null,
            null
        )
        }

        // And provider should fetch media store images values, and map them to pictures
        assertThat(actualPictures).isEqualTo(expectedPictures)
    }
}