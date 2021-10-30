package com.diskin.alon.visuals.catalog.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.catalog.presentation.model.PictureDetail

interface PictureDetailViewModel {

    val pictureDetail: LiveData<PictureDetail>

    val pictureError: LiveEvent<Event>
}