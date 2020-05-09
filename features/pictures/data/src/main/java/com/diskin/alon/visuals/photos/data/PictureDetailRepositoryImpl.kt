package com.diskin.alon.visuals.photos.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.presentation.interfaces.PictureDetailRepository
import com.diskin.alon.visuals.photos.presentation.model.PictureDetail
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import kotlin.NoSuchElementException

class PictureDetailRepositoryImpl @Inject constructor(
    private val provider: DeviceDataProvider<MediaStorePicture>
) : PictureDetailRepository {

    override fun get(uri: Uri): Single<PictureDetail> {
        return provider.getAll()
            .map{ list ->
                val mediaStorePicture = list.find { it.uri == uri } ?:
                throw NoSuchElementException()

                PictureDetail(
                    mediaStorePicture.size.toDouble() / 1000000,
                    Date(mediaStorePicture.added),
                    mediaStorePicture.path,
                    mediaStorePicture.title,
                    mediaStorePicture.width.toInt(),
                    mediaStorePicture.height.toInt()
                )
            }
            .firstOrError()
    }
}