package com.diskin.alon.visuals.di.recyclebin

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemRepository
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsFragment
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsViewModel
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemsViewModelImpl
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
            fragment: TrashedItemsFragment,
            factory: TrashedItemsViewModelProvider
        ): TrashedItemsViewModel {
            return ViewModelProvider(fragment,factory).get(TrashedItemsViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindTrashedItemsRepository(repository: TrashedItemRepositoryImpl): TrashedItemRepository
}