package com.diskin.alon.visuals.catalog.featuretest.play_video

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.visuals.photos.featuretest.R
import com.diskin.alon.visuals.catalog.presentation.controller.VideoPreviewFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.robolectric.Shadows

/**
 * Step definitions of the 'Video is played in device player' scenario.
 */
class PlayVideoOnDeviceSteps : GreenCoffeeSteps() {

    private lateinit var testUri: Uri
    private lateinit var scenario: FragmentScenario<VideoPreviewFragment>

    @Given("^User has public video on device with a uri \"([^\"]*)\"$")
    fun userHasPublicVideoOnDeviceWithAUri(uri: String) {
        // Set test uri
        testUri = Uri.parse(uri)
    }

    @And("^User device has an available video player app$")
    fun userDeviceHasAnAvailableVideoPlayerApp() {
        // Set an available device app stub
        val context = ApplicationProvider.getApplicationContext<Context>()
        val shadowPackageManager = Shadows.shadowOf(
            context.packageManager)!!
        val component = ComponentName("com.example", "Example")

        shadowPackageManager.addActivityIfNotPresent(component)
        shadowPackageManager.addIntentFilterForActivity(component,
            IntentFilter(Intent.ACTION_VIEW).apply {
                addDataType("video/*")
                addCategory(Intent.CATEGORY_DEFAULT)
            }
        )

        // Wait for test looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^Video is opened in preview screen$")
    fun videoIsOpenedInPreviewScreen() {
        // Launch preview fragment with test uri as arg
        val fragmentArgs = Bundle().apply {
            putParcelable(VideoPreviewFragment.KEY_VID_URI,testUri)
        }
        val factory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                return if (loadFragmentClass(classLoader, className) == VideoPreviewFragment::class.java) {
                    VideoPreviewFragment()
                } else {
                    super.instantiate(classLoader, className)
                }
            }
        }
        scenario = FragmentScenario.launchInContainer(
            VideoPreviewFragment::class.java,
            fragmentArgs,
            factory
        )

        // Wait for test looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^User selects to play video$")
    fun userSelectsToPlayVideo() {
        // Play video from preview fragment
        onView(withId(R.id.playVideoButton))
            .perform(click())

        // Wait for test looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Video should be played in device video player app$")
    fun videoShouldBePlayedInDeviceVideoPlayerApp() {
        // Verify implicit intent sent to system from fragment for video playing for test uri
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
        Intents.intended(IntentMatchers.hasData(testUri))
        IntentMatchers.hasType("video/*")
    }
}