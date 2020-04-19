package com.diskin.alon.visuals.videos.presentation.interfaces

import com.diskin.alon.visuals.videos.presentation.model.Video
import io.reactivex.Observable

interface VideoRepository {

    fun getAll(): Observable<List<Video>>
}