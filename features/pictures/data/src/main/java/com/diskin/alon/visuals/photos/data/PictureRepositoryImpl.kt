package com.diskin.alon.visuals.photos.data

import com.diskin.alon.visuals.photos.presentation.Picture
import com.diskin.alon.visuals.photos.presentation.PictureRepository
import io.reactivex.Observable
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val photosProvider: DeviceDataProvider<MediaStorePicture>
) : PictureRepository {

    override fun getAll(): Observable<List<Picture>> {
        return photosProvider.getAll()
            .map { devicePhotos ->
                devicePhotos.map { devicePhoto ->
                    Picture(devicePhoto.uri)
                }
            }
    }
}