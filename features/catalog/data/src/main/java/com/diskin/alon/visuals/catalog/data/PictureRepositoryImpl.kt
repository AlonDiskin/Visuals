package com.diskin.alon.visuals.catalog.data

import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.visuals.catalog.presentation.model.Picture
import com.diskin.alon.visuals.catalog.presentation.interfaces.PictureRepository
import io.reactivex.Observable
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val photosProvider: DeviceMediaProvider<MediaStorePicture>
) : PictureRepository {

    override fun getAll(): Observable<List<Picture>> {
        return photosProvider.getAll()
            .map { devicePictures ->
                devicePictures
                    .sortedByDescending { it.added }
                    .map { devicePicture ->
                        Picture(
                            devicePicture.uri
                        )
                    }
            }
    }
}