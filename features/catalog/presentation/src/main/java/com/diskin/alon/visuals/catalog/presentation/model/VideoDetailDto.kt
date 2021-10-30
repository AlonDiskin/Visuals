package com.diskin.alon.visuals.catalog.presentation.model

/**
 * Data transfer value object holder for user video info.
 */
data class VideoDetailDto(
    val size: Double,
    val added: Long,
    val duration: Long,
    val path: String,
    val title: String,
    val width: Int,
    val height: Int
) {
    init {
        require(size > 0) { "video size must be a positive double value, but was:${size}" }
        require((width > 0 && height > 0)) {
            "video width and height must be positive integers, but got:width-${height},height-${height}"
        }
    }
}
