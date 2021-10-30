package com.diskin.alon.visuals.catalog.featuretest.trash_videos

import android.net.Uri
import android.os.Looper
import android.widget.ImageView
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.catalog.featuretest.util.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.catalog.data.MediaStoreVideo
import com.diskin.alon.visuals.photos.featuretest.R
import com.diskin.alon.visuals.catalog.presentation.controller.VideosBrowserFragment
import com.diskin.alon.visuals.catalog.presentation.util.ThumbnailLoader
import com.google.common.truth.Truth
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import gherkin.ast.TableRow
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import io.reactivex.Observable
import org.hamcrest.CoreMatchers.allOf
import org.robolectric.Shadows
import java.text.SimpleDateFormat
import java.util.*

open class TrashVideosStepsBackground(
    private val mockedVideosProvider: DeviceMediaProvider<MediaStoreVideo>
) : GreenCoffeeSteps() {

    protected lateinit var scenario: FragmentScenario<VideosBrowserFragment>

    open fun userHasPublicVideosOnDevice(rows: List<TableRow>) {
        // Extract test data
        val testDeviceVideos = mutableListOf<MediaStoreVideo>()
        val testData = rows.toMutableList()
        val uriIndex = 0
        val durationIndex = 1
        val dateIndex = 2

        // Remove first row containing column names
        testData.removeAt(0)

        // Create test device videos stub from test data
        testData.forEach { row ->
            val testUri = Uri.parse(row.cells[uriIndex].value!!)
            val added = convertTestDateToTimestamp(row.cells[dateIndex].value!!)
            val duration = convertDurationString(row.cells[durationIndex].value!!)

            testDeviceVideos.add(
                MediaStoreVideo(
                    testUri,
                    added,
                    duration
                )
            )
        }

        // Stub mocked provider to return test videos
        every{ mockedVideosProvider.getAll() } returns Observable.just(testDeviceVideos)
    }

    private fun convertTestDateToTimestamp(testDate: String): Long {
        val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm")
        val d: Date = formatter.parse(testDate) as Date

        return d.time
    }

    private fun convertDurationString(duration: String): Long {
        val sdf = SimpleDateFormat("mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return sdf.parse(duration).time
    }

    protected fun openVideosBrowserScreen() {
        // Mock Thumbnail loader before launching
        mockkObject(ThumbnailLoader)

        // Launch videos browser fragment
        scenario = FragmentScenario.launchInContainer(
            VideosBrowserFragment::class.java,
            null,
            R.style.AppTheme,
            null
        )

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    protected fun verifyTestDataVideosDisplayed(rows: List<TableRow>) {
        // Extract test data and verify expected data shown
        val testData = rows.toMutableList()
        val uriIndex = 0
        val durationIndex = 1

        // Remove first row containing column names
        testData.removeAt(0)

        testData.forEachIndexed { index, row ->
            val testUri = Uri.parse(row.cells[uriIndex].value!!)
            val testDuration = row.cells[durationIndex].value!!

            // Verify video thumb was loaded to image view at current list position
            scenario.onFragment {
                val rv = it.view!!.findViewById<RecyclerView>(R.id.videosList)
                val iv = rv.findViewHolderForAdapterPosition(index)!!.itemView
                    .findViewById<ImageView>(R.id.videoThumb)

                verify { ThumbnailLoader.load(iv,testUri) }
            }

            // Verify view at current list position show expected test duration
            onView(
                withRecyclerView(R.id.videosList)
                    .atPosition(index)
            )
                .check(
                    ViewAssertions.matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.videoDuration),
                                withText(testDuration),
                                isDisplayed()
                            )
                        )
                    )
                )
        }

        // Verify shown videos count equals expected size
        scenario.onFragment {
            val rv = it.view!!.findViewById<RecyclerView>(R.id.videosList)!!
            Truth.assertThat(rv.adapter!!.itemCount).isEqualTo(testData.size)
        }
    }
}