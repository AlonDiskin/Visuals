package com.diskin.alon.visuals.catalog.presentation.util

import android.app.Application
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.catalog.presentation.model.VideoDetail
import com.diskin.alon.visuals.catalog.presentation.model.VideoDetailDto
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Maps video detail data models to presentation models.
 */
class VideoDetailMapper @Inject constructor(private val app: Application) {

    fun mapDetail(dto: VideoDetailDto): VideoDetail {
        return VideoDetail(
            app.getString(R.string.vid_size_template,dto.size.toString()),
            SimpleDateFormat(app.getString(R.string.vid_date_format)).format(Date(dto.added)),
            app.getString(
                R.string.vid_duration_format,
                (dto.duration / 1000 / 60).toInt(),
                (dto.duration / 1000 % 60).toInt()),
            dto.path,
            dto.title,
            app.getString(
                R.string.vid_resolution_template,
                dto.width.toString(),
                dto.height.toString())
        )
    }
}