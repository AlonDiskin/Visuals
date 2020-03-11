package com.diskin.alon.visuals.photos.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [PictureDetailActivity] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class PictureDetailActivityTest {

    // System under test
    private lateinit var scenario: ActivityScenario<PictureDetailActivity>

    // Stub values
    private val picUri: Uri = mockk()

    @Before
    fun setUp() {
        // Mock out image loader
        mockkObject(ImageLoader)

        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val intent = Intent(context, PictureDetailActivity::class.java).apply {
            putExtra(context.getString(R.string.extra_pic_uri),picUri)
        }

        scenario = ActivityScenario.launch<PictureDetailActivity>(intent)
    }

    @Test
    fun showEmptyAppBarTitle_whenResumed() {
        // Given a resumed activity

        // Then activity should not display any title in toolbar

        scenario.onActivity {
            val toolbar = it.findViewById<Toolbar>(R.id.toolbar)!!

            assertThat(toolbar.title).isEqualTo("");
        }
    }


    @Test
    fun showPicture_whenResumed() {
        // Given a resumed activity that was launched with pic uri extra

        // Then activity should ask image loader to load the picture into layout
        scenario.onActivity {
            val uriExtraKey = it.getString(R.string.extra_pic_uri)
            val uri = it.intent.extras?.getParcelable<Uri>(uriExtraKey)!!
            val imageView = it.findViewById<ImageView>(R.id.pictureView)!!

            verify { ImageLoader.loadImage(imageView,uri) }
        }
    }

    @Test
    fun restoreUi_whenRecreated() {
        // Given a resumed activity

        // When activity is recreated
        scenario.recreate()

        // Then picture should be re loaded to image view
        scenario.onActivity {
            val uriExtraKey = it.getString(R.string.extra_pic_uri)
            val uri = it.intent.extras?.getParcelable<Uri>(uriExtraKey)!!
            // This instance of image view is new one,after activity recreation
            val imageView = it.findViewById<ImageView>(R.id.pictureView)!!

            verify { ImageLoader.loadImage(imageView,uri) }
        }
    }
}