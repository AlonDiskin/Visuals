package com.diskin.alon.visuals.pictures

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents.getIntents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.catalog.presentation.controller.PicturesAdapter.PictureViewHolder
import com.diskin.alon.visuals.util.DeviceUtil
import com.diskin.alon.visuals.util.RecyclerViewMatcher.withRecyclerView
import com.google.common.truth.Truth.assertThat
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
class PicturesBrowserFeatureWorkflowSteps : GreenCoffeeSteps() {

    private val testPicturesUri: MutableList<Uri> = mutableListOf()

    @Given("^User has public pictures on device$")
    fun userHasPublicPicturesOnDevice() {
        // Insert test images to test device media store
        val testBitMaps = listOf(
            BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext<Context>().resources,
                R.drawable.image1
            ),
            BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext<Context>().resources,
                R.drawable.image2
            ),
            BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext<Context>().resources,
                R.drawable.image3
            )
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

            testPicturesUri.add(Uri.parse(uri))
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
        onView(withId(R.id.pictures))
            .perform(click())

        // Verify browser screen displayed
        onView(withId(R.id.fragment_pictures_root))
            .check(matches(isDisplayed()))
    }

    @Then("^All user device public pictures are shown by date in descended order$")
    fun allUserDevicePublicPicturesAreShownByDateInAscendOrder() {
        // Verify all test pictures are shown sorted by date added in descending order
        testPicturesUri.reverse()
        testPicturesUri.forEachIndexed { index, uri ->
            onView(withId(R.id.pictures_list))
                .perform(scrollToPosition<PictureViewHolder>(index))

            onView(withRecyclerView(R.id.pictures_list).atPosition(index))
                .check(matches(
                    hasDescendant(
                        allOf(
                            withId(R.id.pictureImage),
                            withTagValue(`is`(uri.toString())),
                            isDisplayed()
                        )
                    )

                ))
        }
    }

    @Then("^Pictures are displayed as before$")
    fun picturesAreDisplayedAsBefore() {
        // Verify all test pictures are shown sorted by date added in descending order
        testPicturesUri.forEachIndexed { index, uri ->
            onView(withId(R.id.pictures_list))
                .perform(scrollToPosition<PictureViewHolder>(index))

            onView(withRecyclerView(R.id.pictures_list).atPosition(index))
                .check(matches(
                    hasDescendant(
                        allOf(
                            withId(R.id.pictureImage),
                            withTagValue(`is`(uri.toString())),
                            isDisplayed()
                        )
                    )

                ))
        }
    }

    @When("^User selects the first listed picture for sharing$")
    fun userSelectsFirstListedPictureForSharing() {
        Thread.sleep(2000)
        // Select first listed picture
        onView(withId(R.id.pictures_list))
            .perform(
                actionOnItemAtPosition<PictureViewHolder>(
                    0,
                    longClick()
                )
            )

        // Click on share menu item
        onView(withContentDescription(R.string.action_share_pictures))
            .perform(click())
    }

    @Then("^App should share picture$")
    fun appShouldSharePicture() {
        // Verify sharing intent is using the android sharing ui sheet
        val expectedIntentPosition = 1

        intended(hasAction(Intent.ACTION_CHOOSER))
        intended(hasExtraWithKey(Intent.EXTRA_INTENT))

        // Verify selected picture uri is included in sharing intent
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = getIntents()[expectedIntentPosition].extras.get(Intent.EXTRA_INTENT) as Intent
        val intentUri = intent.extras.getParcelable<Uri>(Intent.EXTRA_STREAM)!!

        assertThat(intent.action).isEqualTo(Intent.ACTION_SEND)
        assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_image))
        assertThat(intentUri).isEqualTo(testPicturesUri.first())

        // Delete test  from test device storage(if needed)
        testPicturesUri.forEach {
            ApplicationProvider.getApplicationContext<Context>().contentResolver
                .delete(it,null,null)
        }

        // Exit from android share sheet ui
        DeviceUtil.pressBack()
    }
}