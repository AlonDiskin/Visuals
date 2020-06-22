package com.diskin.alon.visuals.di.recyclebin

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.recuclebin.presentation.interfaces.TrashItemRepository
import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashBrowserFragment
import com.diskin.alon.visuals.recuclebin.presentation.viewmodel.TrashBrowserViewModel
import com.diskin.alon.visuals.recuclebin.presentation.viewmodel.TrashBrowserViewModelImpl
import com.diskin.alon.visuals.recyclebin.data.TrashedItemRepositoryImpl
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