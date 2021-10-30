package com.diskin.alon.visuals.catalog.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.visuals.common.presentation.LiveEvent
import com.diskin.alon.visuals.catalog.presentation.model.VideoDetail
import com.diskin.alon.visuals.catalog.presentation.model.VideoInfoError

/**
 * Video ui view model contract
 */
interface VideoDetailViewModel {

    val videoDetail: LiveData<VideoDetail>

    val videoInfoError: LiveEvent<VideoInfoError>
}
