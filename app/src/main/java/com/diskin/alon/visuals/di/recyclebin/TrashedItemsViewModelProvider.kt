package com.diskin.alon.visuals.di.recyclebin

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.catalog.presentation.interfaces.TrashItemRepository
import com.diskin.alon.visuals.catalog.presentation.controller.TrashBrowserFragment
import com.diskin.alon.visuals.catalog.presentation.viewmodel.TrashBrowserViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class TrashedItemsViewModelProvider @Inject constructor(
    fragment: TrashBrowserFragment,
    private val repositoryProvider: Provider<TrashItemRepository>
) : AbstractSavedStateViewModelFactory(fragment,fragment.arguments) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return TrashBrowserViewModelImpl(
            repositoryProvider.get(),
            handle
        ) as T
    }
}