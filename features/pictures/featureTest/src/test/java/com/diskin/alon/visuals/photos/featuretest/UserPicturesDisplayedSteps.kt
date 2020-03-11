package com.diskin.alon.visuals.photos.featuretest

import android.net.Uri
import android.os.Looper.getMainLooper
import androidx.fragment.app.testing.FragmentScenario
import com.diskin.alon.visuals.photos.presentation.ImageLoader
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.presentation.Picture
import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import org.robolectric.Shadows.shadowOf
import io.mockk.mockkObject
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

/**
 * Step definitions for the 'User device photos displayed' scenario.
 */
class UserPicturesDisplayedSteps(
    private val  mockedPicturesProvider: DeviceDataProvider<MediaStorePicture>
) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<PicturesFragment>
    private val testPictures: MutableList<MediaStorePicture> = mutableListOf(
        MediaStorePicture(Uri.parse("test uri 1"),100L),
        MediaStorePicture(Uri.parse("test uri 2"),10L),
        MediaStorePicture(Uri.parse("test uri 3"),90L),
        MediaStorePicture(Uri.parse("test uri 4"),900L)
    )
    private val devicePicturesSubject: Subject<List<MediaStorePicture>> =
        BehaviorSubject.createDefault(testPictures)

    @Given("^User has test pictures on device$")
    fun userHasTestPicturesOnDevice() {
        // Mock out provider for test, and stub it with test pictures data
        every { mockedPicturesProvider.getAll() } returns devicePicturesSubject
    }

    @And("^User opens pictures browser screen$")
    fun userOpensPicturesBrowserScreen() {
        // Mock Pictures loader before launching
        mockkObject(ImageLoader)

        // Launch pictures fragment
        scenario = FragmentScenario.launchInContainer(PicturesFragment::class.java)

        // Wait for main looper to idle
        shadowOf(getMainLooper()).idle()
    }

    @Then("^Pictures browser screen should display pictures by date in descending order$")
    fun picturesBrowserScreenShouldDisplayPicturesByDateInDescendingOrder() {
        // Verify all test pictures are displayed in sorted descending order
        verifyOrder {
            testPictures
                .sortedByDescending { it.added }
                .map { Picture(it.uri) }
                .forEach { ImageLoader.loadImage(any(),it.uri) }
        }
    }

    @When("^User removes one of test pictures from device$")
    fun userRemovesOneOfTestPicturesFromDevice() {
        testPictures.removeAt(0)
        devicePicturesSubject.onNext(testPictures)
    }

    @Then("^Pictures browser screen should update displayed data$")
    fun picturesBrowserScreenShouldUpdateDisplayedData() {
        // Verify displayed pictures are updated
        testPictures
            .map { Picture(it.uri) }
            .forEach { verify { ImageLoader.loadImage(any(),it.uri) } }
    }
}