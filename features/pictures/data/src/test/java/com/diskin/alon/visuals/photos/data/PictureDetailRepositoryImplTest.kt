package com.diskin.alon.visuals.photos.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.photos.presentation.model.PictureDetail
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * [PictureDetailRepositoryImpl] unit test class.
 */
class PictureDetailRepositoryImplTest {

    // System under test
    private lateinit var repository: PictureDetailRepositoryImpl

    // Mocked collaborators
    private val provider = mockk<DeviceMediaProvider<MediaStorePicture>>()

    @Before
    fun setUp() {
        // Init SUT
        repository = PictureDetailRepositoryImpl(provider)
    }

    @Test
    fun loadPictureDetail_whenQueried() {
        // Test case fixture
        val testUri = mockk<Uri>()
        val testMediaStorePicture = MediaStorePicture(
            testUri,
            200L,
            3500000L,
            "title",
            "path",
            200L,
            100L)
        val testDevicePictures = listOf(
            MediaStorePicture(mock {  },100L, 20L," "," ",200L,100L),
            testMediaStorePicture,
            MediaStorePicture(mock {  },400L, 20L," "," ",200L,100L),
            MediaStorePicture(mock {  },10L, 20L," "," ",200L,100L)
        )
        val expected = PictureDetail(
            3.5,
            Date(testMediaStorePicture.added),
            testMediaStorePicture.path,
            testMediaStorePicture.title,
            testMediaStorePicture.width.toInt(),
            testMediaStorePicture.height.toInt()
        )

        every { provider.getAll() } returns Observable.just(testDevicePictures)

        // Given an initialized repository instance

        // When repository is queried for a picture detail
        val actual = repository.get(testUri).blockingGet()

        // Then repository should find the requested picture from all provider pictures

        // And map result to client
        assertThat(actual).isEqualTo(expected)
    }
}