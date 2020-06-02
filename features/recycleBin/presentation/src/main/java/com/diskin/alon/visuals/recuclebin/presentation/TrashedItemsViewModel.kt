package com.diskin.alon.visuals.recuclebin.presentation

import androidx.lifecycle.LiveData

interface TrashedItemsViewModel {

    /**
     * Get observable [TrashedItem] state.
     */
    val trashedItems: LiveData<List<TrashedItem>>

    /**
     * Current [TrashedItem]s filter type.
     */
    var filter: TrashedFilter
}