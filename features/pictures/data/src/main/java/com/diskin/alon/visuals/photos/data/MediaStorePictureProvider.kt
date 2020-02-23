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
            this::fetchPhotos,
            contentResolver,
            PICTURES_PROVIDER_URI
        )
    }

    private fun fetchPhotos(): List<MediaStorePicture> {
        val pictures: MutableList<MediaStorePicture> = arrayListOf()

        // Define result cursor columns
        val columnId = MediaStore.Images.ImageColumns._ID
        val columnDate = MediaStore.Images.ImageColumns.DATE_ADDED

        // Query provider
        val cursor = contentResolver.query(
            PICTURES_PROVIDER_URI,
            arrayOf(columnId,columnDate),
            null,
            null,
            null)!!

        // Extract columns data from cursor
        while (cursor.moveToNext()) {
            val picId = cursor.getInt(cursor.getColumnIndex(columnId))
            val picUri = Uri.parse(PICTURES_PROVIDER_URI.toString().plus("/${picId}"))
            val picDate = cursor.getLong(cursor.getColumnIndex(columnDate))

            pictures.add(
                MediaStorePicture(picUri,picDate)
            )
        }

        // Free cursor
        cursor.close()

        return pictures
    }
}