package com.diskin.alon.visuals.photos.data

import android.content.ContentResolver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [MediaStorePicturesProvider] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class MediaStoreProviderTest {

    // System under test
    private lateinit var provider: MediaStorePicturesProvider

    // Mocked collaborators
    private val contentResolver: ContentResolver = mockk()

    @Before
    fun setUp() {
        // Init SUT
        provider = MediaStorePicturesProvider(contentResolver)
    }

    @Test
    fun createMediaStoreImagesContentObservable_whenPhotosQueried() {
        // Test case fixture
        mockkObject(RxContentProvider)

        // Given an initialized provider

        // When provider is queried for media store photos
        provider.getAll()

        // Then provider should return photos observable created by RxContentProvider, passing
        // it the expected arguments
        verify { RxContentProvider.create(
            any<() -> List<MediaStorePicture>>(),
            contentResolver,
            MediaStorePicturesProvider.PHOTOS_PROVIDER_URI) }
    }
}