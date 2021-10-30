package com.diskin.alon.visuals.catalog.presentation.viewmodel

import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.catalog.presentation.model.VideoDetail
import com.diskin.alon.visuals.catalog.presentation.model.VideoDetailDto
import com.diskin.alon.visuals.catalog.presentation.util.VideoDetailMapper
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.LooperMode
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * [VideoDetailMapper] integration test class.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class VideoDetailMapperTest(private val dto: VideoDetailDto, private val expected: VideoDetail) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Iterable<Array<Any>> {
            return arrayListOf(
                arrayOf(
                    VideoDetailDto(
                        12.3,
                        GregorianCalendar(2020,2,13,20,12).timeInMillis,
                        (TimeUnit.MINUTES.toMillis(1)) + (TimeUnit.SECONDS.toMillis(10)),
                        "path",
                        "title",
                        1200,
                        980
                    ),
                    VideoDetail(
                        "12.3MB",
                        "13 March 2020 20:12",
                        "01:10",
                        "path",
                        "title",
                        "1200*980"
                    )),
                arrayOf(
                    VideoDetailDto(
                        0.45,
                        GregorianCalendar(2017,4,2,1,45).timeInMillis,
                        TimeUnit.SECONDS.toMillis(50),
                        "path",
                        "title",
                        1980,
                        1200
                    ),
                    VideoDetail(
                        "0.45MB",
                        "02 May 2017 01:45",
                        "00:50",
                        "path",
                        "title",
                        "1980*1200"
                    ))
            )
        }
    }

    // System under test
    private lateinit var mapper: VideoDetailMapper

    @Before
    fun setUp() {
        // Create SUT
        mapper = VideoDetailMapper(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun mapVideoDetail_fromDto() {
        // Given an initialized mapper

        // When mapper is asked by client to map a dto to video detail
        val actual = mapper.mapDetail(dto)

        // Then mapper should map dto to video detail as expected
        assertThat(actual).isEqualTo(expected)
    }
}