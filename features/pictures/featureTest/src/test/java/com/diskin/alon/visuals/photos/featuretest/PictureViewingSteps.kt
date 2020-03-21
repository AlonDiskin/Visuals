package com.diskin.alon.visuals.photos.featuretest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.viewpager2.widget.ViewPager2
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.data.MediaStorePicture
import com.diskin.alon.visuals.photos.presentation.controller.PictureViewerActivity
import com.diskin.alon.visuals.photos.presentation.util.ImageLoader
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.hamcrest.CoreMatchers.allOf
import org.robolectric.Shadows
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Step definitions for the 'User views public picture' scenario.
 */
class PictureViewingSteps(
    private val mockedPicturesProvider: DeviceDataProvider<MediaStorePicture>
) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<PictureViewerActivity>
    private val testDevicePictures: MutableList<MediaStorePicture> = mutableListOf()
    private var testSelectedPicturePosition: Int = 0
    private val devicePicturesSubject: Subject<List<MediaStorePicture>> =
        BehaviorSubject.createDefault(testDevicePictures)

    @Given("^User has public pictures on device$")
    fun userHasPublicPicturesOnDevice(rows: List<TableRow>) {
        val testData: MutableList<TableRow> = ArrayList(rows)
        val dateCellIndex = 0
        val sizeCellIndex = 1
        val pathCellIndex = 2
        val titleCellIndex = 3
        val widthCellIndex = 4
        val heightCellIndex = 5

        // remove first row containing column names
        testData.removeAt(0)

        // Extract test data
        testData.forEachIndexed { index, row ->
            val date: String = row.cells[dateCellIndex].value
            val size: Double = row.cells[sizeCellIndex].value.toDouble() * 1000000
            val path: String = row.cells[pathCellIndex].value
            val title: String = row.cells[titleCellIndex].value
            val width: Long = row.cells[widthCellIndex].value.toLong()
            val height: Long = row.cells[heightCellIndex].value.toLong()

            val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm")
            val d: Date = formatter.parse(date) as Date
            val mills: Long = d.time

            testDevicePictures.add(
                MediaStorePicture(
                    Uri.parse("test uri $index"),
                    mills,
                    size.toLong() ,
                    title,
                    path,
                    width,
                    height
                )
            )

            println(mills)
        }

        every{ mockedPicturesProvider.getAll() } returns devicePicturesSubject
    }

    @When("^User open \"([^\"]*)\" picture in picture viewing screen$")
    fun userOpenSelectedPictureInPictureViewingScreen(selected: Int) {
        // Mock Pictures loader before launching
        mockkObject(ImageLoader)

        // Launch picture viewing activity with selected picture uri
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val intent = Intent(context, PictureViewerActivity::class.java).apply {
            putExtra(context.getString(R.string.extra_pic_uri),testDevicePictures[selected].uri)
        }

        scenario = ActivityScenario.launch<PictureViewerActivity>(intent)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Save selected index for future use
        testSelectedPicturePosition = selected
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
        verify { ImageLoader.loadImage(
            any(),
            testDevicePictures[testSelectedPicturePosition].uri,
            any())
        }
    }

    @And("^Selected picture detail is displayed$")
    fun selectedPictureDetailIsDisplayed(rows: List<TableRow>) {
        // Extract expected detail from test data
        val testData: MutableList<TableRow> = ArrayList(rows)
        val dateCellIndex = 0
        val sizeCellIndex = 1
        val pathCellIndex = 2
        val titleCellIndex = 3
        val resolutionCellIndex = 4

        // remove first row containing column names
        testData.removeAt(0)

        val expectedDate = testData[testSelectedPicturePosition].cells[dateCellIndex].value!!
        val expectedSize = testData[testSelectedPicturePosition].cells[sizeCellIndex].value!!
        val expectedPath = testData[testSelectedPicturePosition].cells[pathCellIndex].value!!
        val expectedTitle = testData[testSelectedPicturePosition].cells[titleCellIndex].value!!
        val expectedResolution = testData[testSelectedPicturePosition].cells[resolutionCellIndex].value!!

        // Robolectric\Espresso swipe up os not performed, mimic user swipe up by
        // changing current fragment to detail fragment
        scenario.onActivity {
            val viewPager = it.findViewById<ViewPager2>(R.id.pager)!!

            viewPager.currentItem = 1
        }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Verify test data is displayed
        onView(withId(R.id.sizeDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedSize)
                )
            ))

        onView(withId(R.id.titleDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedTitle)
                )
            ))

        onView(withId(R.id.pathDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedPath)
                )
            ))

        onView(withId(R.id.resolutionDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedResolution)
                )
            ))

        onView(withId(R.id.dateDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(expectedDate)
                )
            ))
    }
}