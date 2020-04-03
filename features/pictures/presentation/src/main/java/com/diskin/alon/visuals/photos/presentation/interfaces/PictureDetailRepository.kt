package com.diskin.alon.visuals.photos.presentation.interfaces

import android.net.Uri
import com.diskin.alon.visuals.photos.presentation.model.PictureDetail
import io.reactivex.Single

interface PictureDetailRepository {

    fun get(uri: Uri): Single<PictureDetail>
}