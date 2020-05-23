package com.diskin.alon.videos.featuretesting.playvideo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.viewpager2.widget.ViewPager2
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.videos.data.MediaStoreVideo
import com.diskin.alon.visuals.videos.presentation.R
import com.diskin.alon.visuals.videos.presentation.controller.VideoDetailActivity
import com.diskin.alon.visuals.videos.presentation.controller.VideosAdapter.VideoViewHolder
import com.diskin.alon.visuals.videos.presentation.controller.VideosBrowserFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import io.mockk.every
import io.reactivex.Observable
import org.hamcrest.CoreMatchers.allOf
import org.robolectric.Shadows
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

/**
 * Step definitions of the 'Video detail is displayed' scenario.
 */
class ProvideVideoDetailSteps(
    private val mockedVideosProvider: DeviceMediaProvider<MediaStoreVideo>
) :  GreenCoffeeSteps() {

    private lateinit var testDeviceVideo: MediaStoreVideo
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private lateinit var videoBrowserScenario: FragmentScenario<VideosBrowserFragment>
    private lateinit var videoDetailScenario: ActivityScenario<VideoDetailActivity>

    @Given("^User has public video on device$")
    fun userHasPublicVideoOnDevice(rows: List<TableRow>) {
        // Create test fixture from test data

        // Extract test data
        val date: String = rows[1].cells[0].value
        val size: Double = rows[1].cells[1].value.toDouble() * 1000000
        val path: String = rows[1].cells[2].value
        val title: String = rows[1].cells[3].value
        val width: Long = rows[1].cells[4].value.toLong()
        val height: Long = rows[1].cells[5].value.toLong()
        val duration: String = rows[1].cells[6].value
        val uri: String = rows[1].cells[7].value

        val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm")
        val d: Date = formatter.parse(date) as Date
        val added: Long = d.time
        val durationMilli = Duration.between(
            LocalTime.MIN ,
            LocalTime.parse( "00:${duration}" )
        ).toMillis()

        testDeviceVideo = MediaStoreVideo(
            Uri.parse(uri),
            added,
            durationMilli,
            size.toLong(),
            title,
            path,
            width,
            height
        )

        // Stub mocked provider to return test data via subject
        every{ mockedVideosProvider.getAll() } returns Observable.just(listOf(testDeviceVideo))
    }

    @And("^User selects video from browser$")
    fun userSelectsVideoFromBrowser() {
        // Setup test nav controller
        navController.setGraph(R.navigation.videos_nav_graph)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.videoDetail) {
                val context = ApplicationProvider.getApplicationContext<Context>()!!
                val intent = Intent(context, VideoDetailActivity::class.java).apply {
                    // Extract vid uri from controller and put it to activity launch intent
                    val vidUriArgKey = context.getString(R.string.extra_vid_uri)
                    val vidUriArg = navController
                        .currentBackStackEntry?.arguments?.getParcelable<Uri>(vidUriArgKey)!!

                    putExtra(vidUriArgKey,vidUriArg)
                }

                // Launch video detail activity when user navigates to it from browser(manually,robolectric bug)
                videoDetailScenario = ActivityScenario.launch(intent)

                // Wait for main looper to idle
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }
        }

        // Launch videos browser fragment
        videoBrowserScenario = FragmentScenario.launchInContainer(VideosBrowserFragment::class.java)
        // Set test nav controller on videos fragment
        videoBrowserScenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Select test video
        onView(withId(R.id.videosList))
            .perform(
                actionOnItemAtPosition<VideoViewHolder>(
                    0,
                    click()
                )
            )

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User select to view video info$")
    fun userSelectToViewVideoInfo() {
        // Open video detail from video detail screen
        // Robolectric\Espresso swipe up os not performed, mimic user swipe up by
        // changing current fragment to detail fragment
        videoDetailScenario.onActivity {
            val viewPager = it.findViewById<ViewPager2>(R.id.pager)!!

            viewPager.currentItem = 1
        }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Video data should be displayed$")
    fun videoDataShouldBeDisplayed(rows: List<TableRow>) {
        // Extract expected test data
        val date: String = rows[1].cells[0].value
        val size: String = rows[1].cells[1].value
        val path: String = rows[1].cells[2].value
        val title: String = rows[1].cells[3].value
        val resolution: String = rows[1].cells[4].value
        val duration: String = rows[1].cells[5].value

        // Verify expected data is shown
        onView(withId(R.id.sizeDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(size)
                    )
                )
            )

        onView(withId(R.id.dateDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(date)
                    )
                )
            )

        onView(withId(R.id.pathDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(path)
                    )
                )
            )

        onView(withId(R.id.titleDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(title)
                    )
                )
            )

        onView(withId(R.id.resolutionDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(resolution)
                    )
                )
            )

        onView(withId(R.id.durationDetail))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(duration)
                    )
                )
            )
    }
}