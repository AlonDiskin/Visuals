package com.diskin.alon.visuals.photos.presentation

import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.LiveEvent

interface PicturesViewModel {

    val photos:LiveData<List<Picture>>

    val photosUpdateError: LiveEvent<String>
}