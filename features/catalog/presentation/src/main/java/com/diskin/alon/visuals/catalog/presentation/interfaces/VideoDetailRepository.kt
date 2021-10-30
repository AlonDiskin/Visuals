package com.diskin.alon.visuals.catalog.presentation.interfaces

import android.net.Uri
import com.diskin.alon.visuals.catalog.presentation.model.VideoDetailDto
import io.reactivex.Single

interface VideoDetailRepository {

    fun get(uri: Uri): Single<VideoDetailDto>
}
