package com.diskin.alon.visuals.catalog.presentation.model

import java.util.*

/**
 * User device picture detail.
 */
data class PictureDetail(
    val size: Double,
    val date: Date,
    val path: String,
    val title: String,
    val width: Int,
    val height: Int
    ) {

    init {
        require(size > 0) { "picture size must be a positive double value, but was:${size}" }
        require((width > 0 && height > 0)) {
            "picture width and height must be positive integers, but got:width-${height},height-${height}"
        }
    }
}