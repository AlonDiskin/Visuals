package com.diskin.alon.visuals.photos.data

import com.diskin.alon.common.data.DeviceDataProvider
import com.diskin.alon.visuals.photos.presentation.model.Picture
import com.diskin.alon.visuals.photos.presentation.interfaces.PictureRepository
import io.reactivex.Observable
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val photosProvider: DeviceDataProvider<MediaStorePicture>
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