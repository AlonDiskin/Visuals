package com.diskin.alon.visuals.recuclebin.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashedFilter
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItem

interface TrashBrowserViewModel {

    /**
     * Get observable [TrashItem] state.
     */
    val trashItems: LiveData<List<TrashItem>>

    /**
     * Current [TrashItem]s filter type.
     */
    var filter: TrashedFilter

    val restoreEvent: LiveEvent<Event>

    val restoreAllEvent: LiveEvent<Event>

    fun restore(itemsUri: List<Uri>)

    /**
     * Restores all current trash items.
     */
    fun restoreAll()
}