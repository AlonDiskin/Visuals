package com.diskin.alon.visuals.photos.featuretest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.presentation.controller.PicturesAdapter.PictureViewHolder
import com.diskin.alon.visuals.photos.presentation.controller.PicturesBrowserFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.robolectric.Shadows

/**
 * Step definitions for the 'User share pictures' scenario.
 */
class SharePicturesSteps(
    private val mockedPicturesProvider: DeviceDataProvider<MediaStorePicture>
) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<PicturesBrowserFragment>
    private val testDevicePictures = mutableListOf(
        MediaStorePicture(
            Uri.parse("test uri 1"),
            10L,
            0L,
            "",
            "",
            0L,
            0L
        ),
        MediaStorePicture(
            Uri.parse("test uri 2"),
            900L,
            0L,
            "",
            "",
            0L,
            0L
        ),
        MediaStorePicture(
            Uri.parse("test uri 3"),
            30L,
            0L,
            "",
            "",
            0L,
            0L
        ),
        MediaStorePicture(
            Uri.parse("test uri 4"),
            140L,
            0L,
            "",
            "",
            0L,
            0L
        )
    )
    private val testSelectedPicturesUri: MutableList<Uri> = mutableListOf()
    private val devicePicturesSubject: Subject<List<MediaStorePicture>> =
        BehaviorSubject.createDefault(testDevicePictures)

    @Given("^User has public pictures on device$")
    fun userHasPublicPicturesOnDevice() {
        every{ mockedPicturesProvider.getAll() } returns devicePicturesSubject
    }

    @And("^User opened the pictures browser screen$")
    fun userOpenedThePicturesBrowserScreen() {
        // Launch pictures fragment
        scenario = FragmentScenario.launchInContainer(PicturesBrowserFragment::class.java)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User selects \"([^\"]*)\" of them")
    fun userSelectsOfThem(picturesNum: Int) {
        val expectedDisplayed = testDevicePictures.sortedByDescending { it.added }
        // Select number pictures(depending on test value)
        for (i in 0 until picturesNum) {
            // long click to start multi selection(for first selected picture),
            // or just click to select
            val action: ViewAction = if (i == 0) longClick() else click()

            onView(ViewMatchers.withId(R.id.pictures_list))
                .perform(scrollToPosition<PictureViewHolder>(i))
                .perform(actionOnItemAtPosition<PictureViewHolder>(i,action))

            testSelectedPicturesUri.add(expectedDisplayed[i].uri)
        }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User share selected pictures$")
    fun userShareSelectedPictures() {
        // Click on share menu item
        onView(withContentDescription(R.string.action_share_pictures))
            .perform(click())
    }

    @Then("^Pictures browser should share the pictures$")
    fun picturesBrowserShouldShareThePictures() {
        // Verify sharing intent is using the android sharing ui sheet
        intended(hasAction(Intent.ACTION_CHOOSER))
        intended(hasExtraWithKey(Intent.EXTRA_INTENT))

        // Verify selected pictures uris are included in sharing intent, taking into account
        // that intent should be build differently for sharing single picture, or multiple
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intents.getIntents().first().extras.get(Intent.EXTRA_INTENT) as Intent
        val intentUris = if (testSelectedPicturesUri.size > 1) {
            intent.extras.getParcelableArrayList(Intent.EXTRA_STREAM)!!
        } else {
            listOf(intent.extras.getParcelable<Uri>(Intent.EXTRA_STREAM)!!)
        }

        if (testSelectedPicturesUri.size > 1) {
            assertThat(intent.action).isEqualTo(Intent.ACTION_SEND_MULTIPLE)
        } else{
            assertThat(intent.action).isEqualTo(Intent.ACTION_SEND)
        }

        assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_image))
        assertThat(intentUris.size).isEqualTo(testSelectedPicturesUri.size)
        testSelectedPicturesUri.forEach { assertThat(intentUris.contains(it)).isTrue() }
    }
}