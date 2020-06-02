package com.diskin.alon.visuals.videos.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.RxContentProvider
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Provider of all user device public videos.
 */
class MediaStoreVideoProvider @Inject constructor(
    private val contentResolver: ContentResolver
) : DeviceMediaProvider<MediaStoreVideo> {

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
        val columnSize = MediaStore.Video.VideoColumns.SIZE
        val columnTitle = MediaStore.Video.VideoColumns.TITLE
        val columnPath = MediaStore.Video.VideoColumns.DATA
        val columnWidth = MediaStore.Video.VideoColumns.WIDTH
        val columnHeight = MediaStore.Video.VideoColumns.HEIGHT

        // Query provider
        val cursor = contentResolver.query(
            VIDEOS_PROVIDER_URI,
            arrayOf(
                columnId,
                columnDate,
                columnDuration,
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
            val videoId = cursor.getInt(cursor.getColumnIndex(columnId))
            val videoUri = Uri.parse(VIDEOS_PROVIDER_URI.toString().plus("/${videoId}"))
            val videoDate = cursor.getLong(cursor.getColumnIndex(columnDate)) * 1000L // convert to milliseconds
            val videoDuration = cursor.getLong(cursor.getColumnIndex(columnDuration))
            val videoSize = cursor.getLong(cursor.getColumnIndex(columnSize))
            val videoTitle = cursor.getString(cursor.getColumnIndex(columnTitle))
            val videoPath = cursor.getString(cursor.getColumnIndex(columnPath))
            val videoWidth = cursor.getLong(cursor.getColumnIndex(columnWidth))
            val videoHeight = cursor.getLong(cursor.getColumnIndex(columnHeight))

            videos.add(
                MediaStoreVideo(
                    videoUri,
                    videoDate,
                    videoDuration,
                    videoSize,
                    videoTitle,
                    videoPath,
                    videoWidth,
                    videoHeight
                )
            )
        }

        // Free cursor
        cursor.close()

        return videos
    }
}