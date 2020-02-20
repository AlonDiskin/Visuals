package com.diskin.alon.visuals.photos.data

import android.net.Uri

/**
 * A data holder for a user public picture from his device.
 *
 * @param uri the picture uri.
 * @param added date added stamp.
 */
data class MediaStorePicture(val uri: Uri, val added: Long)