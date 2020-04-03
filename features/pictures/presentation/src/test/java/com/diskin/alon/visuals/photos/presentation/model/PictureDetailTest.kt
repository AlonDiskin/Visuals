package com.diskin.alon.visuals.photos.presentation.model

import io.mockk.mockk
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * [PictureDetail] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class PictureDetailTest {

    // System under test
    private lateinit var picDetail: PictureDetail

    @Test(expected = IllegalArgumentException::class)
    @Parameters(method = "wrongConstructorParams")
    fun throwException_whenCreatedWithWrongArgs(
        size: Double,
        date: Date,
        path: String,
        title: String,
        width: Int,
        height: Int
    ) {
        // Given an un initialized picture detail ref

        // When picture detail instance is created with wrong arguments
        picDetail = PictureDetail(size, date, path, title, width, height)

        // Then instance should throw an IllegalArgumentException
    }

    private fun wrongConstructorParams() = arrayOf(
        arrayOf(0.0, mockk<Date>(),"","",200,300),
        arrayOf(2.34, mockk<Date>(),"","",200,-300),
        arrayOf(-10.4, mockk<Date>(),"","",-200,-300),
        arrayOf(2.45, mockk<Date>(),"","",-200,300)
    )
}