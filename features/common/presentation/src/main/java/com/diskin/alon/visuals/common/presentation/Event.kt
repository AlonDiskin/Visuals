package com.diskin.alon.visuals.common.presentation

data class Event(val status: Status) {

    enum class Status {
        SUCCESS, FAILURE
    }
}