package com.diskin.alon.visuals.videos.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoDetailRepository
import com.diskin.alon.visuals.videos.presentation.model.VideoDetailDto
import io.reactivex.Single
import javax.inject.Inject

class VideoDetailRepositoryImpl @Inject constructor(
    private val videoProvider: DeviceMediaProvider<MediaStoreVideo>
) : VideoDetailRepository {

    override fun get(uri: Uri): Single<VideoDetailDto> {
        return videoProvider.getAll()
            .map { list ->
                val mediaStoreVideo = list.find { it.uri == uri } ?:
                throw NoSuchElementException()

                VideoDetailDto(
                    mediaStoreVideo.size.toDouble() / 1000000,
                    mediaStoreVideo.added,
                    mediaStoreVideo.duration,
                    mediaStoreVideo.path,
                    mediaStoreVideo.title,
                    mediaStoreVideo.width.toInt(),
                    mediaStoreVideo.height.toInt()
                )
            }
            .firstOrError()
    }
}