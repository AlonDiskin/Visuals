package com.diskin.alon.visuals.photos.featuretest

import android.net.Uri
import android.os.Looper.getMainLooper
import androidx.fragment.app.testing.FragmentScenario
import com.diskin.alon.visuals.photos.presentation.ImageLoader
import com.diskin.alon.visuals.photos.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.presentation.Picture
import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.robolectric.Shadows.shadowOf
import io.mockk.mockkObject
import io.mockk.verify
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
        MediaStorePicture(Uri.parse("test uri 1")),
        MediaStorePicture(Uri.parse("test uri 2")),
        MediaStorePicture(Uri.parse("test uri 3"))
    )
    private val devicePicturesSubject: Subject<List<MediaStorePicture>> =
        BehaviorSubject.createDefault(testPictures)

    @Given("^User has test pictures on device$")
    fun userHasTestPicturesOnDevice() {
        // Mock out provider for test, and stub it with test pictures data
        whenever(mockedPicturesProvider.getAll())
            .doReturn(devicePicturesSubject)
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

    @Then("^Pictures browser screen should display pictures$")
    fun picturesBrowserScreenShouldDisplayPictures() {
        // Verify all test pictures are displayed
        testPictures
            .map { Picture(it.uri) }
            .forEach { verify { ImageLoader.loadImage(any(),it) } }
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
            .forEach { verify { ImageLoader.loadImage(any(),it) } }
    }
}