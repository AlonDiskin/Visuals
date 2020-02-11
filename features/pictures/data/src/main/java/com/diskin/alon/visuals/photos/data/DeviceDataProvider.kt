package com.diskin.alon.visuals.photos.data

import io.reactivex.Observable

/**
 * Provides clients with observable content from user device.
 */
interface DeviceDataProvider<T : Any> {

    /**
     * Get an observable list of [T] data from user device.
     */
    fun getAll(): Observable<List<T>>
}