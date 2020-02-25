package com.diskin.alon.visuals.videos.data

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
class MediaStoreVideoProvider @Inject constructor(
    private val contentResolver: ContentResolver
) : DeviceDataProvider<MediaStoreVideo> {

    companion object {
        val VIDEOS_PROVIDER_URI: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    override fun getAll(): Observable<List<MediaStoreVideo>> {
        return RxContentProvider.create(
            this::fetchPhotos,
            contentResolver,
            VIDEOS_PROVIDER_URI
        )
    }

    private fun fetchPhotos(): List<MediaStoreVideo> {
        val videos: MutableList<MediaStoreVideo> = arrayListOf()

        // Define result cursor columns
        val columnId = MediaStore.Video.VideoColumns._ID
        val columnDate = MediaStore.Video.VideoColumns.DATE_ADDED
        val columnDuration = MediaStore.Video.VideoColumns.DURATION

        // Query provider
        val cursor = contentResolver.query(
            VIDEOS_PROVIDER_URI,
            arrayOf(columnId,columnDate,columnDuration),
            null,
            null,
            null)!!

        // Extract columns data from cursor
        while (cursor.moveToNext()) {
            val videoId = cursor.getInt(cursor.getColumnIndex(columnId))
            val videoUri = Uri.parse(VIDEOS_PROVIDER_URI.toString().plus("/${videoId}"))
            val videoDate = cursor.getLong(cursor.getColumnIndex(columnDate))
            val videoDuration = cursor.getLong(cursor.getColumnIndex(columnDuration))

            videos.add(
                MediaStoreVideo(
                    videoUri,
                    videoDate,
                    videoDuration
                )
            )
        }

        // Free cursor
        cursor.close()

        return videos
    }
}