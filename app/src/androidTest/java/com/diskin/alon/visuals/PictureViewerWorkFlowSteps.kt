package com.diskin.alon.visuals

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.photos.presentation.PicturesAdapter
import com.diskin.alon.visuals.photos.presentation.PicturesAdapter.PictureViewHolder
import com.diskin.alon.visuals.util.DeviceUtil
import com.diskin.alon.visuals.util.RecyclerViewMatcher
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf

/**
 * Step definitions of the picture viewer features scenario.
 */
class PictureViewerWorkFlowSteps : GreenCoffeeSteps() {

    private lateinit var testPictureUri: Uri

    @Given("^User has public pictures on device$")
    fun userHasPublicPicturesOnDevice() {
        // Insert test image to test device media store
        val uri = MediaStore.Images.Media.insertImage(
            ApplicationProvider.getApplicationContext<Context>().contentResolver,
            BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext<Context>().resources,
                R.drawable.image1),
            "test_picture_1",
            null
        )

        testPictureUri = Uri.parse(uri)
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
            CoreMatchers.allOf(
                withText(R.string.nav_pictures),
                isDisplayed()
            )
        )
            .perform(ViewActions.click())

        // Verify browser screen displayed
        onView(withId(R.id.fragment_pictures_root))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @And("^Open the first shown picture$")
    fun openTheFirstShownPicture() {
        onView(withId(R.id.pictures_list))
            .perform(scrollToPosition<PictureViewHolder>(0))
            .perform(actionOnItemAtPosition<PictureViewHolder>(0, click()))
    }

    @Then("^Picture should be displayed in full view in own screen$")
    fun pictureShouldBeDisplayedInFullViewInOwnScreen() {
        // Verify test picture was loaded to screen and is displayed
        onView(withId(R.id.pictureView))
            .check(matches(
                allOf(
                    withTagValue(CoreMatchers.`is`(testPictureUri.toString())),
                    isDisplayed()
                )
            ))

        // Delete test picture from test device
        ApplicationProvider.getApplicationContext<Context>().contentResolver
            .delete(testPictureUri,null,null)
    }
}