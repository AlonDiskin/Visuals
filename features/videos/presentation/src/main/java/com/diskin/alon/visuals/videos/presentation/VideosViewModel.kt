package com.diskin.alon.visuals.videos.presentation

import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.LiveEvent

interface VideosViewModel {

    val videos: LiveData<List<Video>>

    val videosUpdateFail: LiveEvent<String>
}