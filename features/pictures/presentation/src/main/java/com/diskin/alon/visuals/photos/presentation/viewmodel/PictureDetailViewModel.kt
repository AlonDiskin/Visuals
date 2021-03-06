package com.diskin.alon.visuals.photos.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.photos.presentation.model.PictureDetail

interface PictureDetailViewModel {

    val pictureDetail: LiveData<PictureDetail>

    val pictureError: LiveEvent<Event>
}