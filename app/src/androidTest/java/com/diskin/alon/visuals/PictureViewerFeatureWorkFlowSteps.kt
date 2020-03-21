package com.diskin.alon.visuals

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.photos.data.MediaStorePictureProvider
import com.diskin.alon.visuals.photos.presentation.controller.PicturesAdapter.PictureViewHolder
import com.diskin.alon.visuals.util.DeviceUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf
import java.text.SimpleDateFormat
import java.util.*

/**
 * Step definitions of the picture viewer features scenario.
 */
class PictureViewerFeatureWorkFlowSteps : GreenCoffeeSteps() {

    private lateinit var testPictureUri: Uri
    private lateinit var expectedDate: String
    private lateinit var expectedSize: String
    private lateinit var expectedTitle: String
    private lateinit var expectedPath: String
    private lateinit var expectedResolution: String

    @Given("^User has public pictures on device$")
    fun userHasPublicPicturesOnDevice() {
        // Insert test image to test device media store
        val uri = MediaStore.Images.Media.insertImage(
            ApplicationProvider.getApplicationContext<Context>().contentResolver,
            BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext<Context>().resources,
                R.drawable.image1),
            null,
            null
        )
        testPictureUri = Uri.parse(uri)

        // Add additional info that is not auto generated by store
        val context = ApplicationProvider.getApplicationContext<Context>()
        val testSize = 2500000L
        val testWidth = 900L
        val testHeight = 460L
        val values = ContentValues(3)

        values.put(MediaStore.Images.ImageColumns.SIZE, testSize)
        values.put(MediaStore.Images.ImageColumns.WIDTH, testWidth)
        values.put(MediaStore.Images.ImageColumns.HEIGHT, testHeight)

        context.contentResolver.update(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values,
            MediaStore.Images.Media._ID + "= ?",
            arrayOf(testPictureUri.lastPathSegment))

        // Set expected displayed data
        val columnPath = MediaStore.Images.ImageColumns.DATA
        val columnTitle = MediaStore.Images.ImageColumns.TITLE
        val columnDate = MediaStore.Images.ImageColumns.DATE_ADDED

        // Query provider
        val cursor = context.contentResolver.query(
            MediaStorePictureProvider.PICTURES_PROVIDER_URI,
            arrayOf(
                columnTitle,
                columnPath,
                columnDate
            ),
            null,
            null,
            null)!!

        cursor.moveToNext()

        expectedTitle = cursor.getString(cursor.getColumnIndex(columnTitle))
        expectedPath = cursor.getString(cursor.getColumnIndex(columnPath))
        expectedSize = "${(testSize.toDouble() / 1000000)}MB"
        expectedResolution = "${testWidth}*${testHeight}"
        expectedDate = SimpleDateFormat(context.getString(R.string.pic_date_format))
            .format(Date(cursor.getLong(cursor.getColumnIndex(columnDate)) * 1000L))

        cursor.close()
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
    }

    @And("^Picture detail are shown$")
    fun pictureDetailAreShow() {
        // Verify picture size,date added,path on device, name, and resolution
        onView(withId(R.id.pager))
            .perform(swipeUp())

        onView(withId(R.id.sizeDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedSize)
                )
            ))

        onView(withId(R.id.titleDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedTitle)
                )
            ))

        onView(withId(R.id.pathDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedPath)
                )
            ))

        onView(withId(R.id.resolutionDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedResolution)
                )
            ))

        onView(withId(R.id.dateDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedDate)
            )
            ))

        // Delete test picture from test device
        ApplicationProvider.getApplicationContext<Context>().contentResolver
            .delete(testPictureUri,null,null)
    }
}