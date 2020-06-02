package com.diskin.alon.visuals.videos.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.videos.presentation.interfaces.VideoRepository
import com.diskin.alon.visuals.videos.presentation.model.Video
import com.diskin.alon.visuals.videos.presentation.model.VideoDuration
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videosProvider: DeviceMediaProvider<MediaStoreVideo>,
    private val trashedItemDao: TrashedItemDao
) : VideoRepository {

    override fun getAll(): Observable<List<Video>> {
        return Observable.combineLatest(
            videosProvider.getAll(),
            trashedItemDao.getAll(),
            BiFunction<List<MediaStoreVideo>,List<TrashedItemEntity>,List<Video>> { deviceVideos, trashed ->
                deviceVideos.filter { pic ->
                    !trashed.contains(
                        TrashedItemEntity(pic.uri.toString(),
                            TrashedEntityType.VIDEO)) }
                    .sortedByDescending { it.added }
                    .map { deviceVideo ->
                        Video(
                            deviceVideo.uri,
                            VideoDuration(
                                (deviceVideo.duration / 1000 % 60).toInt(),
                                (deviceVideo.duration / 1000 / 60).toInt()
                            )
                        )
                    }
            }
        )
    }

    override fun trash(vararg videoUri: Uri): Completable {
        return trashedItemDao
            .insert(*videoUri
                .map {
                    TrashedItemEntity(it.toString(),TrashedEntityType.VIDEO)
                }
                .toTypedArray()
            )
            .subscribeOn(Schedulers.io())
    }
}