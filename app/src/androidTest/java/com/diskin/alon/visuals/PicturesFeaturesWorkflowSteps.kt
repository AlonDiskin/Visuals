package com.diskin.alon.visuals

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.util.DeviceUtil
import com.diskin.alon.visuals.util.RecyclerViewMatcher.withRecyclerView
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
            // Since content provider stamp image adding date in seconds(not milliseconds),
            // due to processor speed, we need to space adding, or all images will have same stamp
            Thread.sleep(2000)
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

    @Then("^All user device public pictures are shown by date in descended order$")
    fun allUserDevicePublicPicturesAreShownByDateInAscendOrder() {
        // Verify all test pictures are shown sorted by date added in descending order
        testPictures.reverse()
        testPictures.forEachIndexed { index, uri ->
            onView(withRecyclerView(R.id.pictures_list).atPosition(index))
                .check(matches(allOf(
                    withId(R.id.picture),
                    withTagValue(`is`(uri.toString())),
                    isDisplayed())))
        }
    }

    @When("^User rotates device$")
    fun userRotatesDevice() {
        // Rotate test device
        DeviceUtil.rotateDevice()
    }

    @Then("^Pictures are displayed as before$")
    fun picturesAreDisplayedAsBefore() {
        // Verify all test pictures are shown sorted by date added in descending order
        testPictures.forEachIndexed { index, uri ->
            onView(withRecyclerView(R.id.pictures_list).atPosition(index))
                .check(matches(allOf(
                    withId(R.id.picture),
                    withTagValue(`is`(uri.toString())),
                    isDisplayed())))
        }

        // Delete test pictures from test device storage(if needed)
        testPictures.forEach {
            ApplicationProvider.getApplicationContext<Context>().contentResolver
                .delete(it,null,null)
        }
    }
}