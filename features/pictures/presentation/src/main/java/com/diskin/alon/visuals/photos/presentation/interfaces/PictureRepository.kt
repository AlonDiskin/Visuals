package com.diskin.alon.visuals.photos.presentation.interfaces

import com.diskin.alon.visuals.photos.presentation.model.Picture
import io.reactivex.Observable

/**
 * [Picture] repository contract.
 */
interface PictureRepository {

    /**
     * Get an observable list of [Picture], sorted by date in
     * descending order.
     *
     * @return an observable list of [Picture]s.
     */
    fun getAll(): Observable<List<Picture>>
}