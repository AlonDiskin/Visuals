package com.diskin.alon.visuals.photos.presentation

import android.net.Uri
import android.os.Looper.getMainLooper
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.photos.presentation.di.TestApp
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast
import javax.inject.Inject

/**
 * [PicturesFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(application = TestApp::class)
class PicturesFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<PicturesFragment>

    // Mocked SUT collaborator
    @Inject
    lateinit var viewModel: PicturesViewModel

    // Collaborator stubs
    private val photosLiveData = MutableLiveData<List<Picture>>()
    private val photosUpdateError =
        SingleLiveEvent<String>()

    @Before
    fun setUp() {
        // Stub collaborators
        whenever(viewModel.photos).doReturn(photosLiveData)
        whenever(viewModel.photosUpdateError).doReturn(photosUpdateError)

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
