package com.diskin.alon.visuals.recyclebin.data

import android.content.ContentResolver
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diskin.alon.common.data.RxContentProvider
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [MediaStoreVisualProvider] unit test class.
 */
@RunWith(AndroidJUnit4::class)
class MediaStoreVisualProviderTest {

    // System under test
    private lateinit var provider: MediaStoreVisualProvider

    // Mocked collaborators
    private val contentResolver: ContentResolver = mockk()

    @Before
    fun setUp() {
        // Init sut
        provider = MediaStoreVisualProvider(contentResolver)
    }

    @Test
    fun createMediaStoreVideosContentObservable_whenVideosQueried() {
        // Test case fixture
        mockkObject(RxContentProvider)

        // Given an initialized provider

        // When provider is queried for media store videos
        provider.getAll()

        // Then provider should return observable created by merging RxContentProvider
        // observables that observe device media store videos and images
        verify { RxContentProvider.create(
            any<() -> List<MediaStoreVisual>>(),
            contentResolver,
            MediaStoreVisualProvider.VIDEOS_PROVIDER_URI) }

        verify { RxContentProvider.create(
            any<() -> List<MediaStoreVisual>>(),
            contentResolver,
            MediaStoreVisualProvider.PICTURES_PROVIDER_URI) }
    }
}