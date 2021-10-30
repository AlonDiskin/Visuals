package com.diskin.alon.visuals.catalog.featuretest

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.visuals.catalog.presentation.controller.PictureDetailFragment
import com.diskin.alon.visuals.catalog.presentation.interfaces.PictureDetailRepository
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PictureDetailViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class TestPictureDetailViewModelProvider @Inject constructor(
    fragment: PictureDetailFragment,
    private val repository: Provider<PictureDetailRepository>
): AbstractSavedStateViewModelFactory(fragment,fragment.arguments) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return PictureDetailViewModelImpl(repository.get(),handle) as T
    }
}