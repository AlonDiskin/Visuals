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
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.diskin.alon.common.data.AppDatabase
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.R
import com.diskin.alon.visuals.VisualsApp
import com.diskin.alon.visuals.di.app.AppComponent
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItem
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItemType
import com.diskin.alon.visuals.util.DeviceUtil
import com.google.common.truth.Truth
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Recycle bin workflows scenarios background steps.
 */
open class RecycleBinWorkflowsBackground : GreenCoffeeSteps() {

    private var db: AppDatabase
    private val testMediaUri: MutableList<Uri> = mutableListOf()
    private lateinit var testTrashedItems: MutableList<TrashedItemEntity>
    protected lateinit var expectedTrashedItems: MutableList<TrashItem>

    init {
        // Get app db instance
        val app = ApplicationProvider.getApplicationContext<Context>() as VisualsApp
        var field = VisualsApp::class.java.getDeclaredField("appComponent")
        field.isAccessible = true
        db = (field.get(app) as AppComponent).getAppDatabase()

        // Verify test device and app db are empty
        Truth.assertThat(db.trashedDao().getAll().blockingFirst().isEmpty()).isTrue()

        // Turn of data binding
        field = ViewDataBinding::class.java.getDeclaredField("USE_CHOREOGRAPHER")
        field.isAccessible = true

        field.set(null,false)
    }

    open fun userHasTrashedItemsFromDeviceVideosAndPictures() {
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
        Truth.assertThat(db.trashedDao().getAll().blockingFirst().isEmpty()).isFalse()

        // Set expected trashed items expected sorted order to verify are displayed
        expectedTrashedItems = mutableListOf(
            TrashItem(
                testMediaUri.first(),
                TrashItemType.PICTURE
            ),
            TrashItem(
                testMediaUri.last(),
                TrashItemType.VIDEO
            )
        ).asReversed()
    }

    open fun userLaunchAppFromDeviceHomeScreen() {
        // Go to device home screen
        DeviceUtil.openDeviceHome()

        // Launch app
        DeviceUtil.launchApp()
    }

    open fun userNavigatesToRecycleBinScreen() {
        // Navigate to recycle bin screen from home screen
        onView(withId(R.id.recycle_bin))
            .perform(click())
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