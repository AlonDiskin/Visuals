package com.diskin.alon.visuals.videos.data

import android.net.Uri

/**
 * Data holder info of a public device video.
 */
data class MediaStoreVideo(
    val uri: Uri,
    val added: Long ,
    val duration: Long,
    val size: Long = 0L,
    val title: String = "",
    val path: String = "",
    val width: Long = 0L,
    val height: Long = 0L
)