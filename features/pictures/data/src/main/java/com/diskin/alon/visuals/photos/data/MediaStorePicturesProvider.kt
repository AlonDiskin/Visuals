package com.diskin.alon.visuals.photos.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Provider of all public photos, from his device.
 */
class MediaStorePicturesProvider @Inject constructor(
    private val contentResolver: ContentResolver
) : DeviceDataProvider<MediaStorePicture> {

    companion object {
        val PHOTOS_PROVIDER_URI: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    override fun getAll(): Observable<List<MediaStorePicture>> {
        return RxContentProvider.create(
            this::fetchPhotos,
            contentResolver,
            PHOTOS_PROVIDER_URI
        )
    }

    private fun fetchPhotos(): List<MediaStorePicture> {
        val photos: MutableList<MediaStorePicture> = arrayListOf()
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID)
        val cursor = contentResolver.query(
            PHOTOS_PROVIDER_URI,
            projection,
            null,
            null,
            null)!!
        val columnId = MediaStore.Images.ImageColumns._ID

        while (cursor.moveToNext()) {
            val photoId = cursor.getString(cursor.getColumnIndex(columnId)).toInt()
            val photoUri = Uri.parse(PHOTOS_PROVIDER_URI.toString().plus("/${photoId}"))

            photos.add(
                MediaStorePicture(photoUri)
            )
        }

        cursor.close()
        return photos
    }
}