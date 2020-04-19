package com.diskin.alon.visuals.videos.presentation.model

data class VideoDuration(val seconds: Int, val minutes: Int) {

    init {
        require(seconds in 0..59)
        require(minutes in 0..59)
    }

    fun getFormattedDuration(): String {
        return String.format(
            "%02d:%02d",
            minutes,
            seconds
        )
    }
}