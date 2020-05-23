package com.diskin.alon.visuals.recuclebin.presentation

import androidx.lifecycle.LiveData

interface TrashedItemsViewModel {

    val trashedItems: LiveData<List<TrashedItem>>
}