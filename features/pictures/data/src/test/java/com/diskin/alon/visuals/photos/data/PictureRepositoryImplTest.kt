package com.diskin.alon.visuals.photos.data

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.presentation.model.Picture
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * [PictureRepositoryImpl] unit test class.
 */
class PictureRepositoryImplTest {

    // System under test
    private lateinit var repository: PictureRepositoryImpl

    // Mocked collaborators
    @Mock
    lateinit var photosProvider: DeviceDataProvider<MediaStorePicture>

    @Before
    fun setUp() {
        // Init mocks
        MockitoAnnotations.initMocks(this)

        // Init SUT
        repository = PictureRepositoryImpl(photosProvider)
    }

    @Test
    fun returnSortedDescendingPictures_whenPicturesQueried() {
        // Test case fixture
        val testDevicePictures = listOf(
            MediaStorePicture(mock {  },100L, 20L," "," ",200L,100L),
            MediaStorePicture(mock {  },20L, 20L," "," ",200L,100L),
            MediaStorePicture(mock {  },400L, 20L," "," ",200L,100L),
            MediaStorePicture(mock {  },10L, 20L," "," ",200L,100L)
        )
        val expectedPictures = testDevicePictures
            .sortedByDescending { it.added }
            .map { Picture(it.uri) }

        whenever(photosProvider.getAll()).doReturn(Observable.just(testDevicePictures))

        // Given an initialized repository

        // When repository is queried for photos
        val actualPhotos = repository.getAll().blockingFirst()!!

        // Then repository should fetch pictures observable from provider, and return
        // a mapped observable, that emits pictures sorted by date in decs order
        assertThat(actualPhotos).isEqualTo(expectedPictures)
    }
}