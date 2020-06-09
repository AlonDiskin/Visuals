package com.diskin.alon.visuals.photos.presentation.controller

import android.content.Context
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.photos.presentation.model.PictureDetail
import com.diskin.alon.visuals.photos.presentation.viewmodel.PictureDetailViewModel
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast
import java.text.SimpleDateFormat
import java.util.*

/**
 * [PictureDetailFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class PictureDetailFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<PictureDetailFragment>

    // Mocked collaborator
    private val viewModel: PictureDetailViewModel = mockk()

    // Collaborator stubs
    private val pictureDetailData = MutableLiveData<PictureDetail>()
    private val pictureErrorData = SingleLiveEvent<Event>()

    @Before
    fun setUp() {
        // Mock out dagger injection
        mockkStatic(AndroidSupportInjection::class)

        val fragmentSlot = slot<Fragment>()

        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val fragment = fragmentSlot.captured as PictureDetailFragment
            fragment.viewModel = viewModel
        }

        // Stub collaborator
        every { viewModel.pictureDetail } returns pictureDetailData
        every { viewModel.pictureError } returns pictureErrorData

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(PictureDetailFragment::class.java)

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun showPictureDetail_whenResumed() {
        // Given a resumed fragment
        // 1585842307
        // 1584123120000

        // When fragments view model updates its picture detail state
        val context = ApplicationProvider.getApplicationContext<Context>()
        val calendar = GregorianCalendar(2020,2,13,20,12)
        val testPicDetail = PictureDetail(
            3.4,
            Date(calendar.timeInMillis),
            "path",
            "title",
            1200,
            980
            )

        pictureDetailData.value = testPicDetail

        // Then fragment should show detail in its layout
        onView(withId(R.id.sizeDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(context.getString(
                        R.string.pic_size_template,
                        testPicDetail.size.toString()
                        ))
                )
            ))

        onView(withId(R.id.titleDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(testPicDetail.title)
                )
            ))

        onView(withId(R.id.pathDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(testPicDetail.path)
                )
            ))

        onView(withId(R.id.resolutionDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(context.getString(
                        R.string.pic_resolution_template,
                        testPicDetail.width,
                        testPicDetail.height
                    ))
                )
            ))

        onView(withId(R.id.dateDetail))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(
                        SimpleDateFormat(context.getString(R.string.pic_date_format))
                            .format(testPicDetail.date))
                )
            ))
    }

    @Test
    fun notifyUser_whenPictureDetailShowingFail() {
        // Given a resumed fragment

        // When fragments view model raise a picture update error event
        val context = ApplicationProvider.getApplicationContext<Context>()
        pictureErrorData.value = Event(Event.Status.FAILURE)

        // Then fragment should show event error message as toast notification to user
        val actualMessage = ShadowToast.getTextOfLatestToast().toString()
        assertThat(actualMessage)
            .isEqualTo(context.getString(R.string.picture_detail_loading_error))
    }
}