package com.diskin.alon.visuals

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.visuals.util.DeviceUtil
import com.diskin.alon.visuals.util.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.videos.presentation.VideoDuration
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Step definitions for videos browser workflow scenario.
 */
class VideosFeaturesWorkflowSteps : GreenCoffeeSteps() {

    private val testVideosUri = mutableListOf<Uri>()
    private val testDurations = mutableListOf(
        VideoDuration(45,1),
        VideoDuration(30,0)
    )

    @Given("^User has public videos on his device$")
    fun userHasPublicVideosOnHisDevice() {
        // Copy test videos to test device
        val context = ApplicationProvider.getApplicationContext<Context>()
        val testVideos = listOf(
            R.raw.test_video1,
            R.raw.test_video2
        )
        val resolver = context.contentResolver

        testVideos.forEachIndexed { index,  videoId ->
            // Since content provider stamp image adding date in seconds(not milliseconds),
            // due to processor speed, we need to space adding, or all images will have same stamp
            if (index > 0) {
                Thread.sleep(3000)
            }
            val values = ContentValues(4)

            // Copy video from resources to public storage on test device
            val path = "${Environment.getExternalStorageDirectory().absolutePath}/test_video${index}.mp4"
            val videoFile = File(path)
            val readData = ByteArray(1024 * 500)
            val fis: InputStream = context.resources.openRawResource(videoId)
            val fos = FileOutputStream(videoFile)
            var i = fis.read(readData)

            while (i != -1) {
                fos.write(readData, 0, i)
                i = fis.read(readData)
            }

            fos.close()

            // Insert test video file info to media store
            values.put(MediaStore.Video.Media.TITLE, "test_video$index")
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            values.put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
            values.put(MediaStore.Video.Media.DURATION,
                        TimeUnit.SECONDS.toMillis(testDurations[index].seconds.toLong()) +
                        TimeUnit.MINUTES.toMillis(testDurations[index].minutes.toLong())
                )

            // Add media store uri to test videos list
            testVideosUri.add(
                resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)!!
            )
        }
    }

    @Given("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    @And("^User navigates to videos browser screen$")
    fun userNavigatesToVideosBrowserScreen() {
        // Navigate to videos screen from home screen
        onView(allOf(withText(R.string.nav_videos),isDisplayed()))
            .perform(ViewActions.click())

        // Verify browser screen displayed
        onView(withId(R.id.fragment_videos_root))
            .check(matches(isDisplayed()))
    }

    @Then("^All user device public videos should be shown by date in descending order$")
    fun allUserDevicePublicVideosShouldBeShownByDateInDescendingOrder() {
        // Verify all test videos are shown sorted by date added in descending order
        testVideosUri.reverse()
        testDurations.reverse()
        testVideosUri.forEachIndexed { index, uri ->
            onView(withRecyclerView(R.id.videosList).atPosition(index))
                .check(
                    matches(
                        allOf(
                            hasDescendant(
                                allOf(
                                    withId(R.id.videoThumb),
                                    withTagValue(`is`(uri.toString())),
                                    isDisplayed())
                            ),
                            hasDescendant(
                                allOf(
                                    withId(R.id.videoDuration),
                                    withText(testDurations[index].getFormattedDuration()),
                                    isDisplayed())
                            )
                        )
                    )
                )
        }

        // Delete test pictures from test device storage(if needed)
        testVideosUri.forEach {
            ApplicationProvider.getApplicationContext<Context>().contentResolver
                .delete(it,null,null)
        }
    }
}