package com.diskin.alon.visuals.di.recyclebin

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemRepository
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsFragment
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class TrashedItemsViewModelProvider @Inject constructor(
    fragment: TrashedItemsFragment,
    private val repositoryProvider: Provider<TrashedItemRepository>
) : AbstractSavedStateViewModelFactory(fragment,fragment.arguments) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return TrashedItemsViewModelImpl(
            repositoryProvider.get(),
            handle
        ) as T
    }
}