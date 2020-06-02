package com.diskin.alon.visuals.videos.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.videos.presentation.model.Video

interface VideosBrowserViewModel {

    val videos: LiveData<List<Video>>

    val videosUpdateFail: LiveEvent<String>

    val videosTrashedEvent: LiveEvent<Event>

    /**
     * Move [Video]s to recycle bin.
     *
     * @param videoUri selected videos uri.
     */
    fun trashVideos(vararg videoUri: Uri)
}