package com.diskin.alon.visuals.catalog.presentation.interfaces

import android.net.Uri
import com.diskin.alon.visuals.catalog.presentation.model.Video
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * [Video] repository contract.
 */
interface VideoRepository {

    /**
     * Get user device public [Video]s,sorted by date added,in descending order. Videos currently
     * trashed in recycle bin would not be emitted.
     *
     * @return an [Observable] that emits the [Video]s list,
     */
    fun getAll(): Observable<List<Video>>

    /**
     * Moves [Video]s to app recycle bin.
     *
     * @param videoUri uri of the videos to trash.
     */
    fun trash(videosUri: List<Uri>): Single<List<Uri>>

    fun restoreFromTrash(videoUri: List<Uri>): Completable
}