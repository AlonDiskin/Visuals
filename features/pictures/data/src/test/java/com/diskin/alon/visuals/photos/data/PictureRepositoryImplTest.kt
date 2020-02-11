package com.diskin.alon.visuals.photos.data

import com.diskin.alon.visuals.photos.presentation.Picture
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
    fun returnMappedProviderPhotos_whenPhotosQueried() {
        // Test case fixture
        val testDevicePhotos = listOf(
            MediaStorePicture(mock {  }),
            MediaStorePicture(mock {  }),
            MediaStorePicture(mock {  })
        )

        whenever(photosProvider.getAll()).doReturn(Observable.just(testDevicePhotos))

        // Given an initialized repository

        // When repository is queried for photos
        val expectedPhotos = testDevicePhotos.map { Picture(it.uri) }
        val actualPhotos = repository.getAll().blockingFirst()!!

        // Then repository should fetch photos observable from provider, and return
        // a mapped observable, that emits presentation photos
        assertThat(actualPhotos).isEqualTo(expectedPhotos)
    }
}