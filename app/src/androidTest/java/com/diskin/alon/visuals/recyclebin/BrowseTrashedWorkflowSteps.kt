package com.diskin.alon.visuals.recyclebin

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.databinding.ViewDataBinding
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.diskin.alon.common.data.AppDatabase
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.VisualsApp
import com.diskin.alon.visuals.di.app.AppComponent
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItem
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemType
import com.diskin.alon.visuals.util.DeviceUtil
import com.diskin.alon.visuals.util.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.util.isRecyclerViewItemsCount
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Step definitions for the 'Trashed items are listed' scenario.
 */
class BrowseTrashedWorkflowSteps : GreenCoffeeSteps() {

    private var db: AppDatabase
    private val testMediaUri: MutableList<Uri> = mutableListOf()
    private lateinit var testTrashedItems: MutableList<TrashedItemEntity>
    private lateinit var expectedTrashedItems: MutableList<TrashedItem>

    init {
        // Get app db instance
        val app = ApplicationProvider.getApplicationContext<Context>() as VisualsApp
        var field = VisualsApp::class.java.getDeclaredField("appComponent")
        field.isAccessible = true
        db = (field.get(app) as AppComponent).getAppDatabase()

        // Verify test device and app db are empty
        assertThat(db.trashedDao().getAll().blockingFirst().isEmpty()).isTrue()

        // Turn of data binding
        field = ViewDataBinding::class.java.getDeclaredField("USE_CHOREOGRAPHER")
        field.isAccessible = true

        field.set(null,false)
    }

    @Given("^User has trashed items from device videos and pictures$")
    fun userHasTrashedItemsFromDeviceVideosAndPictures() {
        // Insert test images and videos to test device media store
        val context = ApplicationProvider.getApplicationContext<Context>()
        val resolver = context.contentResolver
        val testVideos = listOf(
            R.raw.test_video1,
            R.raw.test_video2
        )
        val testBitMaps = listOf(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.image1),
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.image2),
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.image3)
        )

        testBitMaps.forEach { bitmap ->
            val uri = MediaStore.Images.Media.insertImage(
                resolver,
                bitmap,
                null,
                null
            )

            testMediaUri.add(Uri.parse(uri))
        }

        testVideos.forEach { resId ->
            val videoFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.resources.getResourceEntryName(resId)!!
            )
            val inputStream: InputStream = context.resources.openRawResource(resId)
            val outputStream = FileOutputStream(videoFile)

            outputStream.write(inputStream.readBytes())
            outputStream.close()

            val mediaStoreUri =  resolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply { put(MediaStore.Video.Media.DATA,videoFile.path) }
            )!!

            testMediaUri.add(mediaStoreUri)
        }

        // Set some test device media as trashed in app db
        testTrashedItems = mutableListOf(
            TrashedItemEntity(testMediaUri.first().toString(), TrashedEntityType.PICTURE),
            TrashedItemEntity(testMediaUri.last().toString(), TrashedEntityType.VIDEO)
        )

        db.trashedDao().insert(*testTrashedItems.toTypedArray()).blockingAwait()
        assertThat(db.trashedDao().getAll().blockingFirst().isEmpty()).isFalse()

        // Set expected trashed items expected sorted order to verify are displayed
        expectedTrashedItems = mutableListOf(
            TrashedItem(testMediaUri.first(),TrashedItemType.PICTURE),
            TrashedItem(testMediaUri.last(),TrashedItemType.VIDEO)
        ).asReversed()
    }

    @And("^User launch app from device home screen$")
    fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    @When("^User navigates to recycle bin screen$")
    fun userNavigatesToRecycleBinScreen() {
        // Navigate to recycle bin screen from home screen
        onView(withId(R.id.recycle_bin))
            .perform(click())

        // Verify recycle bin screen displayed
        onView(withId(R.id.recycle_bin_browser_fragment_root))
            .check(matches(isDisplayed()))
    }

    @Then("^All trashed items should be shown sorted by trashing date in desc order$")
    fun allTrashedItemsShouldBeShownSortedByTrashingDateInDescOrder() {
        // Verify test trashed items are displayed as expected
        expectedTrashedItems.forEachIndexed { index, trashedItem ->
            val viewId = when(trashedItem.type) {
                TrashedItemType.VIDEO -> R.id.trashedVideoThumb
                TrashedItemType.PICTURE -> R.id.trashedPicture
            }
            onView(withRecyclerView(R.id.trashedList).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(viewId),
                                withTagValue(CoreMatchers.`is`(trashedItem.uri.toString())),
                                isDisplayed()
                            )
                        )
                    )
                )
        }
    }

    @When("^User filters items to show only trashed pictures$")
    fun userFiltersItemsToShowOnlyTrashedPictures() {
        onView(withId(R.id.action_filter))
            .perform(click())
        onView(withText(R.string.title_filter_image))
            .perform(click())
    }

    @Then("^Only trashed pictures should be displayed$")
    fun onlyTrashedPicturesShouldBeDisplayed() {
        val expectedSize = expectedTrashedItems
            .filter { it.type == TrashedItemType.PICTURE }
            .size

        // Verify all expected items are shown
        expectedTrashedItems
            .filter { it.type == TrashedItemType.PICTURE }
            .forEachIndexed { index, trashedItem ->
                val viewId = when(trashedItem.type) {
                    TrashedItemType.VIDEO -> R.id.trashedVideoThumb
                    TrashedItemType.PICTURE -> R.id.trashedPicture
                }
                onView(withRecyclerView(R.id.trashedList).atPosition(index))
                    .check(
                        matches(
                            hasDescendant(
                                allOf(
                                    withId(viewId),
                                    withTagValue(CoreMatchers.`is`(trashedItem.uri.toString())),
                                    isDisplayed()
                                )
                            )
                        )
                    )
            }

        // Verify only expected items shown
        onView(withId(R.id.trashedList))
            .check(matches(isRecyclerViewItemsCount(expectedSize)))
    }

    fun deleteAllTestDataFromDevice() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressHome()
        db.clearAllTables()
        testMediaUri.forEach {
            ApplicationProvider.getApplicationContext<Context>().contentResolver
                .delete(it,null,null)
        }
    }
}