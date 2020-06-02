package com.diskin.alon.visuals.recuclebin.presentation

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.widget.RelativeLayout
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import dagger.android.support.AndroidSupportInjection
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

/**
 * [TrashedItemsFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class TrashedItemsFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<TrashedItemsFragment>

    // Mocked collaborators
    private val viewModel = mockk<TrashedItemsViewModel>()

    // Stub data
    private val trashedItemsData = MutableLiveData<List<TrashedItem>>()
    private var trashedFilterData = TrashedFilter.ALL

    @Before
    fun setUp() {
        // Mock out dagger
        mockkStatic(AndroidSupportInjection::class)
        val fragmentSlot = slot<TrashedItemsFragment>()
        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val videosFragment = fragmentSlot.captured
            videosFragment.viewModel = viewModel
        }

        // Stub mocked collaborator
        every { viewModel.trashedItems } returns trashedItemsData
        every { viewModel getProperty "filter" } returns trashedFilterData
        every { viewModel setProperty "filter" value any<TrashedFilter>() } propertyType TrashedFilter::class answers { trashedFilterData = value}

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(
            TrashedItemsFragment::class.java,
            null,
            null)
    }

    @Test
    fun showRecycleBinItemsState_whenResumed() {
        // Test case fixture
        mockkStatic("com.diskin.alon.visuals.recuclebin.presentation.BindingAdaptersKt")

        // Given a resumed activity

        // When view model items are updated
        val testItems = listOf(
            TrashedItem(Uri.parse("test_uri_1"),TrashedItemType.VIDEO),
            TrashedItem(Uri.parse("test_uri_2"),TrashedItemType.PICTURE),
            TrashedItem(Uri.parse("test_uri_3"),TrashedItemType.PICTURE),
            TrashedItem(Uri.parse("test_uri_4"),TrashedItemType.VIDEO)
        )

        trashedItemsData.value = testItems
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display items in ui adapter in order of arrival
        verifyAll {
            loadThumbnail(any(),testItems[0].uri)
            loadImage(any(),testItems[1].uri)
            loadImage(any(),testItems[2].uri)
            loadThumbnail(any(),testItems[3].uri)
        }
    }

    @Test
    fun showFilterState_whenResumed() {
        // Test fixture
        val expectedFilterTextRes = when(trashedFilterData) {
            TrashedFilter.ALL -> R.string.title_filter_all
            TrashedFilter.PICTURE -> R.string.title_filter_image
            TrashedFilter.VIDEO -> R.string.title_filter_video
        }

        // Given a resumed fragment

        // Then fragment should display view model filter as the selected filter type
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.title_filter))
            .perform(click())

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(allOf(
            instanceOf(RelativeLayout::class.java),
            hasDescendant(withText(expectedFilterTextRes))
        ))
            .check(matches(hasSibling(isChecked())))
    }

    @Test
    fun changeFilterMenu_whenUserSelectType() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Given a resumed fragment

        // When user selects video type from filter menu
        val videoMenuItem = ActionMenuItem(
            context,
            0,
            R.id.action_filter_video,
            0,
            0,
            null
        )

        scenario.onFragment { it.onOptionsItemSelected(videoMenuItem) }

        // Then fragment should check video filter type menu item
        assertThat(videoMenuItem.isChecked).isTrue()

        // When user selects all type from filter menu
        val allFilterMenuItem = ActionMenuItem(
            context,
            0,
            R.id.action_filter_video,
            0,
            0,
            null
        )

        scenario.onFragment { it.onOptionsItemSelected(allFilterMenuItem) }

        // Then fragment should check all filter type menu item
        assertThat(allFilterMenuItem.isChecked).isTrue()

        // When user selects picture type from filter menu
        val pictureFilterMenuItem = ActionMenuItem(
            context,
            0,
            R.id.action_filter_video,
            0,
            0,
            null
        )

        scenario.onFragment { it.onOptionsItemSelected(pictureFilterMenuItem) }

        // Then fragment should check picture filter type menu item
        assertThat(pictureFilterMenuItem.isChecked).isTrue()
    }

    @Test
    fun filterTrashedItems_whenUserSelectsFilterType() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Given a resumed fragment

        // When user selects 'video' type from filter menu
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(
                ActionMenuItem(context, 0, R.id.action_filter_video, 0, 0, null)
            )
        }

        // Then fragment should ask view model to filter items by video
        verify { viewModel setProperty "filter" value TrashedFilter.VIDEO }

        // When user selects 'picture' type from filter menu
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(
                ActionMenuItem(context, 0, R.id.action_filter_image, 0, 0, null)
            )
        }

        // Then fragment should ask view model to filter items by picture
        verify { viewModel setProperty "filter" value TrashedFilter.PICTURE }

        // When user selects 'all' type from filter menu
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(
                ActionMenuItem(context, 0, R.id.action_filter_all, 0, 0, null)
            )
        }

        // Then fragment should ask view model to filter items by all
        verify { viewModel setProperty "filter" value TrashedFilter.ALL }
    }
}