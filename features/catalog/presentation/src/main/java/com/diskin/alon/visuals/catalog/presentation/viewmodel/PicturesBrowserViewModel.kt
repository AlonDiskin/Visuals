package com.diskin.alon.visuals.catalog.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.catalog.presentation.model.Picture

interface PicturesBrowserViewModel {

    val photos:LiveData<List<Picture>>

    val photosUpdateError: LiveEvent<String>
}