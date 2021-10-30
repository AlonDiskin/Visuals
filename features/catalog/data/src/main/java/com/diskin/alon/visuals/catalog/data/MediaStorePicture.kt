package com.diskin.alon.visuals.catalog.data

import android.net.Uri

/**
 * A data holder for a user public picture from his device.
 *
 * @param uri the picture uri.
 * @param added date added stamp.
 * @param size image size in bytes.
 * @param title image file name on device.
 * @param path file device path.
 * @param width image width in pixels.
 * @param height image height in pixels.
 */
data class MediaStorePicture(
    val uri: Uri,
    val added: Long,
    val size: Long,
    val title: String,
    val path: String,
    val width: Long,
    val height: Long
)