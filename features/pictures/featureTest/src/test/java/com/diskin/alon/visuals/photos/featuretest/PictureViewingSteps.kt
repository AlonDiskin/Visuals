package com.diskin.alon.visuals.photos.featuretest

import android.net.Uri
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.presentation.ImageLoader
import com.diskin.alon.visuals.photos.presentation.PictureDetailActivity
import com.diskin.alon.visuals.photos.presentation.PicturesAdapter.PictureViewHolder
import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.robolectric.Shadows

/**
 * Step definitions for the 'User views public picture' scenario.
 */
class PictureViewingSteps(
    private val mockedPicturesProvider: DeviceDataProvider<MediaStorePicture>
) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<PicturesFragment>
    private lateinit var pictureDetailActivityScenario: ActivityScenario<PictureDetailActivity>
    private val testDevicePictures = mutableListOf(
        MediaStorePicture(
            Uri.parse("test uri 1"),
            10L
        ),
        MediaStorePicture(
            Uri.parse("test uri 2"),
            900L
        ),
        MediaStorePicture(
            Uri.parse("test uri 3"),
            30L
        ),
        MediaStorePicture(
            Uri.parse("test uri 4"),
            140L
        )
    )
    private val testSelectedPicturesUri: MutableList<Uri> = mutableListOf()
    private val devicePicturesSubject: Subject<List<MediaStorePicture>> =
        BehaviorSubject.createDefault(testDevicePictures)

    @Given("^User has public pictures on device$")
    fun userHasPublicPicturesOnDevice() {
        every{ mockedPicturesProvider.getAll() } returns devicePicturesSubject
    }

    @When("^User opens an existing picture to view it$")
    fun userOpensAnExistingPictureToViewIt() {
        // Mock Pictures loader before launching
        mockkObject(ImageLoader)

        // Launch pictures fragment
        scenario = FragmentScenario.launchInContainer(PicturesFragment::class.java)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        Intents.init()

        // Select the first displayed picture
        onView(withId(R.id.pictures_list))
            .perform(RecyclerViewActions.scrollToPosition<PictureViewHolder>(0))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<PictureViewHolder>(
                    0,
                    click()
                )
            )

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Launch PictureDetailActivity click operation has started activity
        if (Intents.getIntents().size == 1) {
            pictureDetailActivityScenario =
                ActivityScenario.launch<PictureDetailActivity>(Intents.getIntents()[0])
        }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        Intents.release()
    }

    @Then("^Picture is shown in picture viewing screen$")
    fun pictureIsShownInPictureViewingScreen() {
        // Verify the selected pic is displayed in PictureDetailActivity

        // Sort test device pictures to expected display order
        testDevicePictures
            .sortByDescending { it.added }

        // Check picture image view of detail activity is displayed
        onView(withId(R.id.pictureView))
            .check(matches(isDisplayed()))

        // Verify picture is loaded to view
        verify(exactly = 2){ ImageLoader.loadImage(any(),testDevicePictures[0].uri) }
    }
}