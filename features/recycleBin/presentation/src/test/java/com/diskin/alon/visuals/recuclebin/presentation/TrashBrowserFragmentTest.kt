package com.diskin.alon.visuals.recuclebin.presentation

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.recuclebin.presentation.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashItemsAdapter.TrashItemViewHolder
import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashBrowserFragment
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashedFilter
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItem
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItemType
import com.diskin.alon.visuals.recuclebin.presentation.util.loadImage
import com.diskin.alon.visuals.recuclebin.presentation.util.loadThumbnail
import com.diskin.alon.visuals.recuclebin.presentation.viewmodel.TrashBrowserViewModel
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

/**
 * [TrashBrowserFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class TrashBrowserFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<TrashBrowserFragment>

    // Mocked collaborators
    private val viewModel = mockk<TrashBrowserViewModel>()

    // Stub data
    private val trashItemsData = MutableLiveData<List<TrashItem>>()
    private var trashedFilterData = TrashedFilter.ALL
    private val restoreEventData = SingleLiveEvent<Event>()
    private val testItems = listOf(
        TrashItem(
            Uri.parse(
                "test_uri_1"
            ), TrashItemType.VIDEO
        ),
        TrashItem(
            Uri.parse(
                "test_uri_2"
            ), TrashItemType.PICTURE
        ),
        TrashItem(
            Uri.parse(
                "test_uri_3"
            ), TrashItemType.PICTURE
        ),
        TrashItem(
            Uri.parse(
                "test_uri_4"
            ), TrashItemType.VIDEO
        )
    )

    @Before
    fun setUp() {
        // Mock out dagger
        mockkStatic(AndroidSupportInjection::class)
        val fragmentSlot = slot<TrashBrowserFragment>()
        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val videosFragment = fragmentSlot.captured
            videosFragment.viewModel = viewModel
        }

        // Stub mocked collaborator
        every { viewModel.trashItems } returns trashItemsData
        every { viewModel getProperty "filter" } returns trashedFilterData
        every { viewModel setProperty "filter" value any<TrashedFilter>() } propertyType TrashedFilter::class answers { trashedFilterData = value}
        every { viewModel.restore(any()) } returns Unit
        every { viewModel.restoreEvent } returns restoreEventData
        every { viewModel.restoreAll() } returns Unit

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(
            TrashBrowserFragment::class.java,
            null,
            R.style.AppTheme,
            null)
    }

    @Test
    fun showTrashedItemsState_whenResumed() {
        // Test case fixture
        mockkStatic("com.diskin.alon.visuals.recuclebin.presentation.util.BindingAdaptersKt")

        // Given a resumed activity

        // When view model items are updated
        trashItemsData.value = testItems
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display items media uris in layout
        verify {
            loadThumbnail(any(),testItems[0].uri)
            loadImage(any(),testItems[1].uri)
            loadImage(any(),testItems[2].uri)
            loadThumbnail(any(),testItems[3].uri)
        }
    }

    @Test
    fun resolveFilterUiState_whenResumed() {
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

    @Test
    fun showTrashItemsMultiSelection_whenItemsMultiSelected() {
        // Given a resumed fragment and displayed items
        displayTestTrashItems()

        // When user multi selects item
        val selectedPositions = listOf(0,1,2)
        selectDisplayedVideos(selectedPositions)

        // Then fragment should show items selection ui as selected
        selectedPositions.forEach { scrollToTrashItemAndVerifyMultiSelected(it) }

        // And fragment should show multi selection ui for all other as non selected
        testItems.forEachIndexed { index, _ ->
            if (!selectedPositions.contains(index)) {
                scrollToTrashItemAndVerifyNotMultiSelected(index)
            }
        }
    }

    @Test
    fun hideTrashItemsMultiSelection_whenAllItemsUnMultiSelected() {
        // Given a resumed fragment and displayed items
        displayTestTrashItems()

        // When user multi selects items
        val selectedPositions = listOf(0,2)
        selectDisplayedVideos(selectedPositions)

        // And then un selects them all
        selectedPositions.forEachIndexed { _, position ->
            scrollToTrashItemAndPerform(position, click())
        }

        // Then fragment should hide multi selection ui from all trashed items
        testItems.forEachIndexed { position, _ ->
            scrollToTrashItemAndCheck(
                position,
                hasDescendant(
                    allOf(
                        withId(R.id.selectable_foreground),
                        withEffectiveVisibility(Visibility.INVISIBLE)
                    )
                )
            )
        }
    }

    @Test
    fun showContextualActionMenu_whenFirstItemMultiSelected() {
        // Given a resumed fragment and displayed items
        trashItemsData.value = testItems
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user multi selects item
        onView(withId(R.id.trashList))
            .perform(actionOnItemAtPosition<TrashItemViewHolder>(0,longClick()))

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show contextual action bar
        onView(withContentDescription("Done"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun closeContextualActionMenu_whenAllItemsUnMultiSelected() {
        // Given a resumed fragment and displayed items
        trashItemsData.value = testItems
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user multi selects items
        onView(withId(R.id.trashList))
            .perform(actionOnItemAtPosition<TrashItemViewHolder>(0,longClick()))

        // And un selects them all
        onView(withId(R.id.trashList))
            .perform(actionOnItemAtPosition<TrashItemViewHolder>(0,click()))

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should close its contextual action bar
        onView(withContentDescription("Done"))
            .check(doesNotExist())
    }

    @Test
    fun closeContextualActionMenu_whenFragmentViewDestroyed() {
        // Given a resumed fragment
        scenario.onFragment {
            val activity = it.activity!!

            // And contextual action bar shown
            activity.startActionMode(it)!!

            // When fragment view is destroyed
            it.onDestroyView()
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should close its contextual action bar
        onView(withContentDescription("Done"))
            .check(doesNotExist())
    }

    @Test
    fun restoreMultiSelectedItems_whenUserRestoreSelectedViaActionMenu() {
        // Given a resumed fragment,with displayed trash items
        displayTestTrashItems()

        // When user multi select some trash items
        val selectedPositions = listOf(0,2)
        selectDisplayedVideos(selectedPositions)

        // And user click on 'restore' menu
        onView(withContentDescription(R.string.action_restore))
            .perform(click())

        // Then fragment should ask view model to restore all selected items
        val expectedRestoreItems = testItems.filterIndexed { index, _ ->
            selectedPositions.contains(index) }
            .map { it.uri }
        verify { viewModel.restore(expectedRestoreItems) }
    }

    @Test
    fun restoreAllShownTrashItems_whenUserSelectToRestoreAll() {
        // Given a resumed fragment,with displayed trash items
        displayTestTrashItems()

        // When user click on 'restore' options menu item
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.action_restore_all))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show alert dialog for user confirmation for restoring all items
        val dialog = (ShadowAlertDialog.getLatestDialog() as AlertDialog)
        assertThat(dialog.isShowing).isTrue()

        // When user approve dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should ask view model to restore all trash items
        verify { viewModel.restoreAll() }
    }

    private fun selectDisplayedVideos(selectedVideosIndex: List<Int>) {
        selectedVideosIndex.forEachIndexed { index, testTrashItemsIndex ->
            // If this is the first selected item, initiate multi selection by long click,
            // else just click on item
            val action: ViewAction = if (index == 0) longClick() else click()
            scrollToTrashItemAndPerform(testTrashItemsIndex,action)
        }
    }

    private fun scrollToTrashItem(position: Int) {
        // Scroll to requested trash items list position
        onView(withId(R.id.trashList))
            .perform(scrollToPosition<TrashItemViewHolder>(position))
    }

    private fun scrollToTrashItemAndCheck(position: Int, viewMatcher: Matcher<View>) {
        // Scroll to requested trash items list position
        scrollToTrashItem(position)

        // Perform requested checking on view at position
        onView(withRecyclerView(R.id.trashList).atPosition(position))
            .check(matches(viewMatcher))
    }

    private fun scrollToTrashItemAndPerform(position: Int, viewAction: ViewAction) {
        onView(withId(R.id.trashList))
            .perform(actionOnItemAtPosition<TrashItemViewHolder>(position, viewAction))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    private fun scrollToTrashItemAndVerifyMultiSelected(position: Int) {
        scrollToTrashItemAndCheck(position,
            hasDescendant(
                CoreMatchers.allOf(
                    withId(R.id.select_item_checkBox),
                    isChecked(),
                    isDisplayed()
                )
            ))
    }

    private fun scrollToTrashItemAndVerifyNotMultiSelected(position: Int) {
        scrollToTrashItemAndCheck(position,
            hasDescendant(
                CoreMatchers.allOf(
                    withId(R.id.select_item_checkBox),
                    isNotChecked(),
                    isDisplayed()
                )
            ))
    }

    private fun displayTestTrashItems() {
        trashItemsData.value = testItems
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
}