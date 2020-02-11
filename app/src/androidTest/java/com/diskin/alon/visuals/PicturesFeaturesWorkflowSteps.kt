package com.diskin.alon.visuals

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.photos.presentation.PicturesAdapter.PictureViewHolder
import com.diskin.alon.visuals.util.DeviceUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf

/**
 * Step definitions for photos browser workflow scenarios.
 */
class PicturesFeaturesWorkflowSteps : GreenCoffeeSteps() {

    private val testPictures: MutableList<Uri> = mutableListOf()

    @Given("^User has public pictures on device$")
    fun userHasPublicPicturesOnDevice() {
        // Insert test images to test device media store
        val testBitMaps = listOf(
            BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext<Context>().resources,
                R.drawable.image1),
            BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext<Context>().resources,
                R.drawable.image2),
            BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext<Context>().resources,
                R.drawable.image3)
        )

        testBitMaps.forEachIndexed { index, bitmap ->
            val uri = MediaStore.Images.Media.insertImage(
                ApplicationProvider.getApplicationContext<Context>().contentResolver,
                bitmap,
                "test_picture${index + 1}",
                null
            )

            testPictures.add(Uri.parse(uri))
        }
    }

    @And("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    @When("^User navigates to pictures browser screen$")
    fun userNavigatesToPicturesBrowserScreen() {
        // Navigate to pictures browser screen
        onView(
            allOf(
                withText(R.string.nav_pictures),
                isDisplayed()
            )
        )
            .perform(ViewActions.click())

        // Verify browser screen displayed
        onView(withId(R.id.fragment_pictures_root))
            .check(matches(isDisplayed()))
    }

    @Then("^All user device public pictures are shown by date in ascend order$")
    fun allUserDevicePublicPictuesAreShownByDateInAscendOrder() {
        // Verify all test pictures are shown
        testPictures.forEachIndexed { index, uri ->
            onView(withId(R.id.pictures_list))
                .perform(RecyclerViewActions.scrollToPosition<PictureViewHolder>(index))

            onView(allOf(withId(R.id.picture), withTagValue(`is`(uri.toString()))))
                .check(matches(isDisplayed()))
        }

        // Delete test pictures from test device storage(if needed)
        if (testPictures.isNotEmpty()) {
            testPictures.forEach {
                ApplicationProvider.getApplicationContext<Context>().contentResolver
                    .delete(it,null,null)
            }
        }
    }
}