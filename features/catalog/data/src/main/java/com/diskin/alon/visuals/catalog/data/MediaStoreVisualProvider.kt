package com.diskin.alon.visuals.catalog.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.RxContentProvider
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

/**
 * Provides an observable to users public images and videos from his device.
 */
class MediaStoreVisualProvider @Inject constructor(
    private val contentResolver: ContentResolver
) : DeviceMediaProvider<MediaStoreVisual> {

    companion object {
        val PICTURES_PROVIDER_URI: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val VIDEOS_PROVIDER_URI: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    override fun getAll(): Observable<List<MediaStoreVisual>> {
        return Observable.combineLatest(
            RxContentProvider.create(
                this::fetchPictures,
                contentResolver,
                PICTURES_PROVIDER_URI
            ),
            RxContentProvider.create(
                this::fetchVideos,
                contentResolver,
                VIDEOS_PROVIDER_URI
            ),
            BiFunction<List<MediaStoreVisual>,List<MediaStoreVisual>,List<MediaStoreVisual>> { t1, t2 ->
                t1.plus(t2)
            }
        )
    }

    private fun fetchPictures(): List<MediaStoreVisual> {
        val pictures: MutableList<MediaStoreVisual> = arrayListOf()

        // Define result cursor columns
        val columnId = MediaStore.MediaColumns._ID

        // Query provider
        val cursor = contentResolver.query(
            PICTURES_PROVIDER_URI,
            arrayOf(columnId),
            null,
            null,
            null)!!

        // Extract columns data from cursor
        while (cursor.moveToNext()) {
            val picId = cursor.getInt(cursor.getColumnIndex(columnId))
            val picUri = Uri.parse(PICTURES_PROVIDER_URI.toString().plus("/${picId}"))

            pictures.add(MediaStoreVisual(picUri))
        }

        // Free cursor
        cursor.close()

        return pictures
    }

    private fun fetchVideos(): List<MediaStoreVisual> {
        val videos: MutableList<MediaStoreVisual> = arrayListOf()

        // Define result cursor columns
        val columnId = MediaStore.MediaColumns._ID

        // Query provider
        val cursor = contentResolver.query(
            VIDEOS_PROVIDER_URI,
            arrayOf(columnId),
            null,
            null,
            null)!!

        // Extract columns data from cursor
        while (cursor.moveToNext()) {
            val videoId = cursor.getInt(cursor.getColumnIndex(columnId))
            val videoUri = Uri.parse(VIDEOS_PROVIDER_URI.toString().plus("/${videoId}"))

            videos.add(MediaStoreVisual(videoUri))
        }

        // Free cursor
        cursor.close()

        return videos
    }
}