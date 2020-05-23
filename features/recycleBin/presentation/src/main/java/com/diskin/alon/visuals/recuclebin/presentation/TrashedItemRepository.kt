package com.diskin.alon.visuals.recuclebin.presentation

import io.reactivex.Observable

/**
 * [TrashedItem] repository contract.
 */
interface TrashedItemRepository {

    /**
     * Return an observable that emit a list of all items,
     * sorted by added order, in descending order.
     */
    fun getAll(): Observable<List<TrashedItem>>
}
