package com.diskin.alon.visuals.videos.data

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.videos.presentation.Video
import com.diskin.alon.visuals.videos.presentation.VideoDuration
import com.diskin.alon.visuals.videos.presentation.VideoRepository
import io.reactivex.Observable
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videosProvider: DeviceDataProvider<MediaStoreVideo>
) : VideoRepository {

    override fun getAll(): Observable<List<Video>> {
        return videosProvider.getAll()
            .map { deviceVideos ->
                deviceVideos
                    .sortedByDescending { it.added }
                    .map { deviceVideo ->
                        Video(deviceVideo.uri,
                            VideoDuration(
                                (deviceVideo.duration / 1000 % 60).toInt(),
                                (deviceVideo.duration / 1000 / 60).toInt()
                            )
                        )
                    }
            }
    }
}