package com.diskin.alon.common.data

import io.reactivex.Observable

/**
 * Provides clients with observable media content from user device.
 */
interface DeviceMediaProvider<T : Any> {

    /**
     * Get an observable list of [T] data from user device.
     */
    fun getAll(): Observable<List<T>>
}