package com.diskin.alon.visuals.recuclebin.presentation

import io.reactivex.Observable

/**
 * [TrashedItem] repository contract.
 */
interface TrashedItemRepository {

    /**
     * Return an observable that emit a list of all [TrashedItem]s,
     * sorted by added order, in descending order, with optional
     * filtering param allowing to produce observables that emit only
     * trashed videos/pictures.
     *
     * @param filter items filtering type.
     */
    fun getAll(filter: TrashedFilter = TrashedFilter.ALL): Observable<List<TrashedItem>>
}
