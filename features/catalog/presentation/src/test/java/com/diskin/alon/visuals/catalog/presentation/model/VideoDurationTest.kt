package com.diskin.alon.visuals.catalog.presentation.model

import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [VideoDuration] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class VideoDurationTest {

    @Test(expected = IllegalArgumentException::class)
    @Parameters(method = "illegalArgsParams")
    fun throwException_whenInitWithWrongArgs(sec: Int, min: Int) {
        // Given un initialized video duration class
        var videoDuration: VideoDuration? = null

        // When video duration class is initialized with wrong min and sec values
        videoDuration =
            VideoDuration(
                sec,
                min
            )

        // Then video duration class should throw an IllegalArgumentException
    }

    @Test
    @Parameters(method = "formatParams")
    fun formatVideoDuration_whenRequested(sec: Int, min: Int, format: String) {
        // Given an initialized video duration class
        val videoDuration =
            VideoDuration(
                sec,
                min
            )

        // When class is asked to return its duration as a string format
        val actualFormat = videoDuration.getFormattedDuration()

        // Then class should return duration in the expected format
        assertThat(actualFormat).isEqualTo(format)
    }

    fun illegalArgsParams() = arrayOf(
        arrayOf(-1,60),
        arrayOf(-2,-1),
        arrayOf(60,100),
        arrayOf(70,-100)
    )

    fun formatParams() = arrayOf(
        arrayOf(10,0,"00:10"),
        arrayOf(0,5,"05:00"),
        arrayOf(37,2,"02:37"),
        arrayOf(1,0,"00:01"),
        arrayOf(0,1,"01:00")
    )
}