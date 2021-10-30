package com.diskin.alon.visuals.catalog.presentation.controller

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.catalog.presentation.util.ImageLoader
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.catalog.presentation.util.MyFragmentFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

/**
 * [PictureFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class PictureFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<PictureFragment>

    // Stub values
    private val picUri: Uri = mockk()
    private val picLoadingErrorSlot = slot<() -> Unit>()

    @Before
    fun setUp() {
        // Mock out image loader
        mockkObject(ImageLoader)

        // Stub mock
        every { ImageLoader.loadImage(any(),any(),capture(picLoadingErrorSlot)) } answers {}

        // Launch fragment under test
        val fragmentArgs = Bundle().apply {
            putParcelable(PictureFragment.KEY_PIC_URI,picUri)
        }
        val factory =
            MyFragmentFactory()

        scenario = FragmentScenario.launchInContainer(
            PictureFragment::class.java,
            fragmentArgs,
            factory
        )
    }

    @Test
    fun showPicture_whenResumed() {
        // Given a resumed fragment

        // Then fragment should load picture via loading a uri arg from its bundle
        scenario.onFragment {
            val imageView = it.view?.findViewById<ImageView>(R.id.pictureView)!!

            verify {
                ImageLoader.loadImage(
                    imageView,
                    picUri,
                    any()
                )
            }
        }
    }

    @Test
    fun restoreUi_whenRecreated() {
        // Given a resumed fragment

        // When fragment is recreated
        scenario.recreate()

        // Then fragment should restore its ui state
        scenario.onFragment {
            val imageView = it.view?.findViewById<ImageView>(R.id.pictureView)!!

            verify {
                ImageLoader.loadImage(
                    imageView,
                    picUri,
                    any()
                )
            }
        }
    }

    @Test
    fun notifyUser_whenPictureImageLoadingFail() {
        // Given a resumed fragment

        // When picture image loading fails
        picLoadingErrorSlot.captured.invoke()

        // Then activity should show a pre defined error message to user
        val context = ApplicationProvider.getApplicationContext<Context>().applicationContext
        val actualMessage = ShadowToast.getTextOfLatestToast().toString()

        assertThat(actualMessage).isEqualTo(context.getString(R.string.picture_image_loading_error))
    }
}