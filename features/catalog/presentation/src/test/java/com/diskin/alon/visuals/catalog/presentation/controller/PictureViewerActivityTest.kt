package com.diskin.alon.visuals.catalog.presentation.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.photos.presentation.TestFragment1
import com.diskin.alon.visuals.photos.presentation.TestFragment2
import com.diskin.alon.visuals.catalog.presentation.util.ImageLoader
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjection
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * [PictureViewerActivity] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class PictureViewerActivityTest {

    // System under test
    private lateinit var scenario: ActivityScenario<PictureViewerActivity>

    // Mocked collaborators
    private val fragmentFactory: PictureViewerFragmentFactory = mockk()

    // Stub values
    private val picUri: Uri = mockk()

    @Before
    fun setUp() {
        // Mock out image loader
        mockkObject(ImageLoader)

        // Mock out dagger injection
        mockkStatic(AndroidInjection::class)

        val activitySlot = slot<Activity>()

        every { AndroidInjection.inject(capture(activitySlot)) } answers {
            val pictureDetailActivity = activitySlot.captured as PictureViewerActivity
            pictureDetailActivity.fragmentsFactory = fragmentFactory
        }

        // Stub mocked collaborator behaviour
        every { fragmentFactory.createPictureViewFragment(picUri) } returns TestFragment1()
        every { fragmentFactory.createPictureDetailFragment(picUri) } returns TestFragment2()

        // Launch activity under test
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val intent = Intent(context, PictureViewerActivity::class.java).apply {
            putExtra(context.getString(R.string.extra_pic_uri),picUri)
        }

        scenario = ActivityScenario.launch(intent)
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
    fun showPicture_whenCreated() {
        // Given a resumed activity

        // Then activity should show a fragment to display picture in full view
        verify { fragmentFactory.createPictureViewFragment(picUri) }

        onView(withText(R.string.test_fragment1_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showPictureDetail_whenUserSwipesScreenUp() {
        // Given a resumed activity

        // When user swipes up
        onView(withId(R.id.pager))
            .perform(swipeUp())

        // Then activity should show a fragment that displays picture detail
        verify { fragmentFactory.createPictureDetailFragment(picUri) }

        onView(withText(R.string.test_fragment2_text))
            .check(matches(isDisplayed()))
    }
}