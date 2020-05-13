package com.diskin.alon.visuals.videos.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.videos.presentation.model.Video

interface VideosBrowserViewModel {

    val videos: LiveData<List<Video>>

    val videosUpdateFail: LiveEvent<String>
}