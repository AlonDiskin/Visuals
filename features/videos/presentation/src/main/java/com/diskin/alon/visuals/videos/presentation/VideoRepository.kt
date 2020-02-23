package com.diskin.alon.visuals.videos.presentation

import io.reactivex.Observable

interface VideoRepository {

    fun getAll(): Observable<List<Video>>
}