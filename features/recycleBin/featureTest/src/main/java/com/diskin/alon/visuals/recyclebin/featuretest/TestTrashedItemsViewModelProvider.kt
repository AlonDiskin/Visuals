package com.diskin.alon.visuals.recyclebin.featuretest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemRepository
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class TestTrashedItemsViewModelProvider @Inject constructor(
    private val repositoryProvider: Provider<TrashedItemRepository>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrashedItemsViewModelImpl(
            repositoryProvider.get()
        ) as T
    }
}