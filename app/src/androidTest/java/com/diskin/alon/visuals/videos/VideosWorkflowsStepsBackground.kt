package com.diskin.alon.visuals.videos

import android.content.ContentValues
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.util.DeviceUtil
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Videos features scenarios common step definitions
 */
abstract class VideosWorkflowsStepsBackground : GreenCoffeeSteps() {

    private val testVideosUri = mutableListOf<Uri>()

    init {
        // Verify test device has no prev public videos
        val context = ApplicationProvider.getApplicationContext<Context>()
        val contentResolver = context.contentResolver!!

        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.MediaColumns._ID),
            null,
            null
        )!!

        val videosCount = cursor.count

        cursor.close()
        assertThat(videosCount).isEqualTo(0)
    }

    open fun userHasPublicVideosOnHisDevice() {
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
                Thread.sleep(1000)
            }

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
            val values = ContentValues(7)
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            val videoWidth =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toLong()
            val videoHeight =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toLong()
            val videoDuration =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            retriever.release()

            values.put(MediaStore.Video.Media.TITLE, videoFile.name)
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            values.put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
            values.put(MediaStore.Video.Media.DURATION,videoDuration)
            values.put(MediaStore.Video.Media.SIZE, videoFile.length())
            values.put(MediaStore.Video.Media.WIDTH, videoWidth)
            values.put(MediaStore.Video.Media.HEIGHT, videoHeight)

            // Add media store uri to test videos list
            testVideosUri.add(
                resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)!!
            )
        }

        // Sort test data to expected ui order
        getTestVideosUri().reverse()
    }

    open fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    open fun userNavigatesToVideosBrowserScreen() {
        // Navigate to videos screen from home screen
        onView(withId(R.id.videos))
            .perform(click())

        // Verify browser screen displayed
        onView(withId(R.id.fragment_videos_root))
            .check(matches(isDisplayed()))
    }

    fun getTestVideosUri(): MutableList<Uri> {
        return testVideosUri
    }

    fun deleteTestDataFromDevice() {
        // Delete test pictures from test device storage
        getTestVideosUri().forEach {
            ApplicationProvider.getApplicationContext<Context>().contentResolver
                .delete(it,null,null)
        }
    }
}