package com.diskin.alon.visuals.recuclebin.presentation

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashBrowserFragment
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashedFilter
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItem
import com.diskin.alon.visuals.recuclebin.presentation.viewmodel.TrashBrowserViewModel
import dagger.android.support.AndroidSupportInjection
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class ExampleInstrumentedTest {

    private lateinit var scenario: FragmentScenario<TrashBrowserFragment>
    private val viewModel = mockk<TrashBrowserViewModel>()
    private val trashedItemsData = MutableLiveData<List<TrashItem>>()
    private var trashedFilterData = TrashedFilter.ALL
    @Before
    fun setUp() {
        mockkStatic(AndroidSupportInjection::class)
        val fragmentSlot = slot<TrashBrowserFragment>()
        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val videosFragment = fragmentSlot.captured
            videosFragment.viewModel = viewModel
        }
        //every { viewModel.trashedItems } returns trashedItemsData
        every { viewModel getProperty "filter" } returns trashedFilterData
        every { viewModel setProperty "filter" value any<TrashedFilter>() } propertyType TrashedFilter::class answers { trashedFilterData = value}

        scenario = FragmentScenario.launchInContainer(
            TrashBrowserFragment::class.java,
            null,
            null)
    }

    @Test
    fun test() {
        scenario.onFragment {
            val actionBar = it.activity?.actionBar
            val activity = it.activity!!

            //assertThat(actionBar).isNotNull()
            //activity.openOptionsMenu()
        }

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.title_filter))
            .perform(click())
        onView(withText(R.string.title_filter_video))
            .perform(click())
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.title_filter))
            .perform(click())
        Thread.sleep(4000L)
    }
}
