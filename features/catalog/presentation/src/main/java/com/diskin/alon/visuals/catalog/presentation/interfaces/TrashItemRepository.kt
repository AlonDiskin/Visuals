package com.diskin.alon.visuals.catalog.presentation.interfaces

import android.net.Uri
import com.diskin.alon.visuals.catalog.presentation.model.TrashedFilter
import com.diskin.alon.visuals.catalog.presentation.model.TrashItem
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * [TrashItem] repository contract.
 */
interface TrashItemRepository {

    /**
     * Return an observable that emit a list of all [TrashItem]s,
     * sorted by added order, in descending order, with optional
     * filtering param allowing to produce observables that emit only
     * trashed videos/pictures.
     *
     * @param filter items filtering type.
     */
    fun getAll(filter: TrashedFilter = TrashedFilter.ALL): Observable<List<TrashItem>>

    fun restore(items: List<Uri>): Completable

    fun restoreAll(): Completable
}
