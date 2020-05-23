package com.diskin.alon.visuals.photos.featuretest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.presentation.controller.PictureViewerActivity
import com.diskin.alon.visuals.photos.presentation.util.ImageLoader
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast

/**
 * Step definitions for the 'Picture viewing fail' scenario.
 */
class PictureViewingFailSteps(
    private val mockedPicturesProvider: DeviceMediaProvider<MediaStorePicture>
) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<PictureViewerActivity>
    private val devicePicturesSubject: Subject<List<MediaStorePicture>> =
        BehaviorSubject.create()
    private val picLoadingErrorSlot = slot<() -> Unit>()
    private val toastsMessages = mutableListOf<String>()

    @Given("^User has opened picture viewing screen to view existing picture$")
    fun userHasOpenedPictureViewingScreenToViewExistingPicture() {
        // Mock Pictures loader before launching
        mockkObject(ImageLoader)

        // Stub mock
        every { ImageLoader.loadImage(any(),any(),capture(picLoadingErrorSlot)) } answers {}

        // Stub mocked media pictures provider
        every { mockedPicturesProvider.getAll() } returns devicePicturesSubject

        // Launch picture viewing activity with selected picture uri
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val intent = Intent(context, PictureViewerActivity::class.java).apply {
            putExtra(context.getString(R.string.extra_pic_uri), mockk<Uri>())
        }

        scenario = ActivityScenario.launch<PictureViewerActivity>(intent)
    }

    @When("^App fail to load existing picture for viewing$")
    fun appFailToLoadExistingPictureForViewing() {
        // create image loading error
        picLoadingErrorSlot.captured.invoke()
        toastsMessages.add(ShadowToast.getTextOfLatestToast())

        // Open picture detail screen
        // Robolectric\Espresso swipe up os not performed, mimic user swipe up by
        // changing current fragment to detail fragment
        scenario.onActivity {
            val viewPager = it.findViewById<ViewPager2>(R.id.pager)!!

            viewPager.currentItem = 1
        }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Create picture detail loading error
        devicePicturesSubject.onError(Throwable())

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        toastsMessages.add(ShadowToast.getTextOfLatestToast())
    }

    @Then("^User should see an error message displayed$")
    fun userShouldSeeAnErrorMessageDisplayed() {
        // Verify image loading error, and picture detail loading error was displayed
        val context = ApplicationProvider.getApplicationContext<Context>().applicationContext

        assertThat(toastsMessages).contains(context.getString(R.string.picture_image_loading_error))
        assertThat(toastsMessages).contains(context.getString(R.string.picture_detail_loading_error))
    }
}