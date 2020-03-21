package com.diskin.alon.visuals.photos.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.common.data.RxContentProvider
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Provider of all public photos, from his device.
 */
class MediaStorePictureProvider @Inject constructor(
    private val contentResolver: ContentResolver
) : DeviceDataProvider<MediaStorePicture> {

    companion object {
        val PICTURES_PROVIDER_URI: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    override fun getAll(): Observable<List<MediaStorePicture>> {
        return RxContentProvider.create(
            this::fetchPictures,
            contentResolver,
            PICTURES_PROVIDER_URI
        )
    }

    private fun fetchPictures(): List<MediaStorePicture> {
        val pictures: MutableList<MediaStorePicture> = arrayListOf()

        // Define result cursor columns
        val columnId = MediaStore.Images.ImageColumns._ID
        val columnDate = MediaStore.Images.ImageColumns.DATE_ADDED
        val columnSize = MediaStore.Images.ImageColumns.SIZE
        val columnTitle = MediaStore.Images.ImageColumns.TITLE
        val columnPath = MediaStore.Images.ImageColumns.DATA
        val columnWidth = MediaStore.Images.ImageColumns.WIDTH
        val columnHeight = MediaStore.Images.ImageColumns.HEIGHT

        // Query provider
        val cursor = contentResolver.query(
            PICTURES_PROVIDER_URI,
            arrayOf(
                columnId,
                columnDate,
                columnSize,
                columnTitle,
                columnPath,
                columnWidth,
                columnHeight
            ),
            null,
            null,
            null)!!

        // Extract columns data from cursor
        while (cursor.moveToNext()) {
            val picId = cursor.getInt(cursor.getColumnIndex(columnId))
            val picUri = Uri.parse(PICTURES_PROVIDER_URI.toString().plus("/${picId}"))
            val picDate = cursor.getLong(cursor.getColumnIndex(columnDate)) * 1000L // convert to milliseconds
            val picSize = cursor.getLong(cursor.getColumnIndex(columnSize))
            val picTitle = cursor.getString(cursor.getColumnIndex(columnTitle))
            val picPath = cursor.getString(cursor.getColumnIndex(columnPath))
            val picWidth = cursor.getLong(cursor.getColumnIndex(columnWidth))
            val picHeight = cursor.getLong(cursor.getColumnIndex(columnHeight))

            pictures.add(
                MediaStorePicture(
                    picUri,
                    picDate,
                    picSize,
                    picTitle,
                    picPath,
                    picWidth,
                    picHeight)
            )
        }

        // Free cursor
        cursor.close()

        return pictures
    }
}