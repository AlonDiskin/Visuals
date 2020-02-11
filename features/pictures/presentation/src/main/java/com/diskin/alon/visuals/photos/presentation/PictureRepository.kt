package com.diskin.alon.visuals.photos.presentation

import io.reactivex.Observable

interface PictureRepository {

    fun getAll(): Observable<List<Picture>>
}