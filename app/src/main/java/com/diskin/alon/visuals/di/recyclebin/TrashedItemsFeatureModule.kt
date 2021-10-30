package com.diskin.alon.visuals.di.recyclebin

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.catalog.presentation.interfaces.TrashItemRepository
import com.diskin.alon.visuals.catalog.presentation.controller.TrashBrowserFragment
import com.diskin.alon.visuals.catalog.presentation.viewmodel.TrashBrowserViewModel
import com.diskin.alon.visuals.catalog.presentation.viewmodel.TrashBrowserViewModelImpl
import com.diskin.alon.visuals.catalog.data.TrashedItemRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class TrashedItemsFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideTrashedItemsViewModel(
            fragment: TrashBrowserFragment,
            factory: TrashedItemsViewModelProvider
        ): TrashBrowserViewModel {
            return ViewModelProvider(fragment,factory).get(TrashBrowserViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindTrashedItemsRepository(repository: TrashedItemRepositoryImpl): TrashItemRepository
}