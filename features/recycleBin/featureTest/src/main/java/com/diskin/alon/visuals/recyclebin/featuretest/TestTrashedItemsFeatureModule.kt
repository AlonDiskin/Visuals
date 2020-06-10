package com.diskin.alon.visuals.recyclebin.featuretest

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
abstract class TestTrashedItemsFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideTrashedItemsViewModel(
            fragment: TrashBrowserFragment,
            factory: TestTrashedItemsViewModelProvider
        ): TrashBrowserViewModel {
            return ViewModelProvider(fragment,factory).get(TrashBrowserViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindTrashedItemsRepository(repository: TrashedItemRepositoryImpl): TrashItemRepository
}