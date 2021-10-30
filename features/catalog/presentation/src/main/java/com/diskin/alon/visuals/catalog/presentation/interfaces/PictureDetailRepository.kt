package com.diskin.alon.visuals.catalog.presentation.interfaces

import android.net.Uri
import com.diskin.alon.visuals.catalog.presentation.model.PictureDetail
import io.reactivex.Single

interface PictureDetailRepository {

    fun get(uri: Uri): Single<PictureDetail>
}