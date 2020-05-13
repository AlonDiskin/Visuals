package com.diskin.alon.videos.featuretesting.listvideos

import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.presentation.controller.VideosBrowserFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast

/**
 * Step definitions for the 'App fail to fetch device videos' scenario.
 */
class VideosFetchFailSteps(
    private val  mockedPicturesProvider: DeviceDataProvider<MediaStoreVideo>
) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<VideosBrowserFragment>
    private val deviceVideosSubject: Subject<List<MediaStoreVideo>> = BehaviorSubject.create()
    private val errorMessage = "pictures loading fail!"

    @Given("^User opens videos browser screen$")
    fun userOpensVideosBrowserScreen() {
        // Stub videos provider mock
        every { mockedPicturesProvider.getAll() } returns deviceVideosSubject

        // Launch videos fragment
        scenario = FragmentScenario.launchInContainer(VideosBrowserFragment::class.java)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^App fail to fetch device videos$")
    fun appFailToFetchDeviceVideos() {
        // Emit device videos error
        deviceVideosSubject.onError(Throwable(errorMessage))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^User should see an error message displayed$")
    fun userShouldSeeAnErrorMessageDisplayed() {
        // Verify expected error message is displayed
        assertThat(ShadowToast.getTextOfLatestToast().toString()).isEqualTo(errorMessage)
    }
}