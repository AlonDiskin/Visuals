package com.diskin.alon.visuals.catalog.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.catalog.presentation.model.Video

interface VideosBrowserViewModel {

    val videos: LiveData<List<Video>>

    val videosUpdateFail: LiveEvent<String>

    val videosTrashedEvent: LiveEvent<Event>

    val videosTrashUndoEvent: LiveEvent<Event>

    /**
     * Move [Video]s to recycle bin.
     *
     * @param videoUri selected videos uri.
     */
    fun trashVideos(videosUri: List<Uri>)

    /**
     * Restore the last trashed videos.
     */
    fun undoLastTrash()
}