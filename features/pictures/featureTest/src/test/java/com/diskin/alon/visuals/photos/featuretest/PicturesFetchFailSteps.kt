package com.diskin.alon.visuals.photos.featuretest

import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import com.diskin.alon.visuals.photos.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.presentation.PicturesFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast

/**
 * Step definitions for the 'App fail to fetch device pictures' scenario.
 */
class PicturesFetchFailSteps(
    private val  mockedPicturesProvider: DeviceDataProvider<MediaStorePicture>
) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<PicturesFragment>
    private val devicePicturesSubject: Subject<List<MediaStorePicture>> = BehaviorSubject.create()
    private val errorMessage = "pictures loading fail!"

    @Given("^User opens pictures browser screen$")
    fun userOPensPicturesBrowserScreen() {
        // Stub pictures provider mock
        whenever(mockedPicturesProvider.getAll()).doReturn(devicePicturesSubject)

        // Launch photos fragment
        scenario = FragmentScenario.launchInContainer(PicturesFragment::class.java)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^App fail to fetch device pictures$")
    fun appFailToFetchDevicePictures() {
        // Emit device pictures error
        devicePicturesSubject.onError(Throwable(errorMessage))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^User should see an error message displayed$")
    fun userShouldSeeAnErrorMessageDisplayed() {
        // Verify expected error message is displayed
        assertThat(ShadowToast.getTextOfLatestToast().toString()).isEqualTo(errorMessage)
    }
}