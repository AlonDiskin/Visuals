package com.diskin.alon.visuals.photos.presentation

import android.net.Uri
import android.os.Looper.getMainLooper
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

/**
 * [PicturesFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class PicturesFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<PicturesFragment>

    // Mocked SUT collaborator
    private val viewModel: PicturesViewModel = mockk()

    // Collaborator stubs
    private val photosLiveData = MutableLiveData<List<Picture>>()
    private val photosUpdateError = SingleLiveEvent<String>()

    @Before
    fun setUp() {
        // Mock out dagger injection
        mockkStatic(AndroidSupportInjection::class)

        val fragmentSlot = slot<Fragment>()

        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val picturesFragment = fragmentSlot.captured as PicturesFragment
            picturesFragment.viewModel = viewModel
        }

        // Stub mocked collaborator behaviour
        every{ viewModel.photos } returns photosLiveData
        every { viewModel.photosUpdateError } returns  photosUpdateError

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(PicturesFragment::class.java)
    }

    @Test
    fun showPhotos_whenPhotosUpdated() {
        // Test case fixture
        mockkObject(ImageLoader)

        // Given a resumed fragment

        // When view model update photos
        val testPhotos = arrayListOf(
            Picture(Uri.parse("uri 1")),
            Picture(Uri.parse("uri 2")),
            Picture(Uri.parse("uri 3"))
        )

        photosLiveData.value = testPhotos
        shadowOf(getMainLooper()).idle()

        // Then fragment should show updated photos in layout
        testPhotos.forEach {
            verify { ImageLoader.loadImage(any(),it) }
        }
    }

    @Test
    fun showErrorMessage_whenPhotosUpdateFail() {
        // Given a resumed fragment

        // When view model raise an update failure event
        val testFailMessage = "fail message"

        photosUpdateError.value = testFailMessage
        shadowOf(getMainLooper()).idle()

        // Then fragment should show a pre defined error message
        assertThat(ShadowToast.getTextOfLatestToast().toString()).isEqualTo(testFailMessage)
    }
}
