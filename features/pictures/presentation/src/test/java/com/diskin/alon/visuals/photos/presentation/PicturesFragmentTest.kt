package com.diskin.alon.visuals.photos.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper.getMainLooper
import android.view.ActionMode
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.visuals.common.presentation.SingleLiveEvent
import com.diskin.alon.visuals.photos.presentation.PicturesAdapter.PictureViewHolder
import com.diskin.alon.visuals.photos.presentation.RecyclerViewMatcher.withRecyclerView
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast.getTextOfLatestToast

/**
 * [PicturesFragment] unit test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
class PicturesFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<PicturesFragment>

    // Mocked SUT collaborator
    private val viewModel: PicturesViewModel = mockk()

    // Collaborator stubs
    private val picturesLiveData = MutableLiveData<List<Picture>>()
    private val picturesUpdateError = SingleLiveEvent<String>()
    private val testPictures = arrayListOf(
        Picture(Uri.parse("uri 1")),
        Picture(Uri.parse("uri 2")),
        Picture(Uri.parse("uri 3")),
        Picture(Uri.parse("uri 4")),
        Picture(Uri.parse("uri 5")),
        Picture(Uri.parse("uri 6")),
        Picture(Uri.parse("uri 7")),
        Picture(Uri.parse("uri 8"))
    )

    @Before
    fun setUp() {
        // Mock out dagger injection
        mockkStatic(AndroidSupportInjection::class)

        val fragmentSlot = slot<Fragment>()

        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            val picturesFragment = fragmentSlot.captured as PicturesFragment
            picturesFragment.viewModel = viewModel
        }

        // Stub mocked collaborator behaviour
        every{ viewModel.photos } returns picturesLiveData
        every { viewModel.photosUpdateError } returns  picturesUpdateError

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(PicturesFragment::class.java)
    }

    @Test
    fun showPictures_whenPicturesUpdated() {
        // Test case fixture
        mockkObject(ImageLoader)

        // Given a resumed fragment

        // When view model update pictures
        displayTestPictures()

        // Then fragment should show updated photos in layout
        testPictures.forEach { verify { ImageLoader.loadImage(any(),it.uri) } }
    }

    @Test
    fun showErrorMessage_whenPhotosUpdateFail() {
        // Given a resumed fragment

        // When view model raise an update failure event
        val testFailMessage = "fail message"
        picturesUpdateError.value = testFailMessage

        shadowOf(getMainLooper()).idle()

        // Then fragment should show a pre defined error message
        val actualMessage = getTextOfLatestToast().toString()
        assertThat(actualMessage).isEqualTo(testFailMessage)
    }

    @Test
    fun showPictureSelectionAndActionUi_whenUserStartPictureMultiSelection() {
        // Given a resumed fragment

        // And displayed pictures
        displayTestPictures()

        // When user long click on a displayed picture
        val clickedIndex = 0

        scrollToPictureAndPerform(clickedIndex,longClick())

        // Then fragment should show selectable foreground ui for each picture
        testPictures.forEachIndexed { index, _ ->
            scrollToPictureAndCheck(
                index,
                allOf(
                    hasDescendant(
                        allOf(
                            withId(R.id.selectable_foreground),
                            withEffectiveVisibility(Visibility.VISIBLE)
                        )
                    ),
                    hasDescendant(
                        allOf(
                            withId(R.id.select_item_checkBox),
                            withEffectiveVisibility(Visibility.VISIBLE)
                        )
                    )
                )
            )
        }

        // And display contextual action bar with menu with actions for selected picture
        scenario.onFragment {
            val field = PicturesFragment::class.java.getDeclaredField("actionMode")
            field.isAccessible = true
            val actionMode: ActionMode =  field.get(it) as ActionMode

            assertThat(actionMode.menu.getItem(0).title)
                .isEqualTo(it.getString(R.string.action_share_pictures))
            assertThat(actionMode.menu.getItem(0).isEnabled)
                .isTrue()
        }
    }

    @Test
    fun checkPictureItems_whenUserMultiSelect() {
        // Given a resumed fragment

        // And displayed pictures
        displayTestPictures()

        // When user selects multiple pictures from displayed list
        val selectedPicturesIndex = listOf(0,testPictures.size - 1)

        selectDisplayedPictures(selectedPicturesIndex)

        // Then fragment should check selected pictures only
        testPictures.forEachIndexed { index, _ ->
            // If test picture was selected, verify check box is checked,
            // otherwise check box should be unchecked
            val check = if (selectedPicturesIndex.contains(index)) {
                hasDescendant(
                    allOf(
                        withId(R.id.select_item_checkBox),
                        isChecked(),
                        isDisplayed())
                )
            } else{
                hasDescendant(
                    allOf(
                        withId(R.id.select_item_checkBox),
                        isNotChecked(),
                        isDisplayed())
                )
            }

            scrollToPictureAndCheck(index,check)
        }
    }

    @Test
    fun unCheckPicture_whenUnSelected() {
        // Given a resumed fragment

        // And displayed pictures
        displayTestPictures()

        // When user selects pictures
        val selectedIndex = listOf(0,2,5)
        val unSelectedIndex = selectedIndex.last()

        selectDisplayedPictures(selectedIndex)

        // And un select a picture item
        scrollToPictureAndPerform(unSelectedIndex, click())

        // Then fragment should set picture item check box as unchecked
        scrollToPictureAndCheck(
            unSelectedIndex,
            hasDescendant(
                allOf(
                    withId(R.id.select_item_checkBox),
                    isNotChecked(),
                    isDisplayed())
            ))
    }

    @Test
    fun sharePictures_whenUserSharesSelectedPictures() {
        // Test case fixture
        Intents.init()

        // Given a resumed fragment

        // And displayed pictures
        displayTestPictures()

        // When user selects pictures
        val selectedIndex = listOf(2,3,5)

        selectDisplayedPictures(selectedIndex)

        // And clicks on share menu item
        onView(withContentDescription(R.string.action_share_pictures))
            .perform(click())

        // Then fragment should share pictures via implicit intent action

        // Verify fragment end share intent using android sharing sheet ui(ui chooser)
        intended(hasAction(Intent.ACTION_CHOOSER))
        intended(hasExtraWithKey(Intent.EXTRA_INTENT))

        // Verify share intent has the expected action,type, and contains the selected pictures uri
        val intent = Intents.getIntents().first().extras.get(Intent.EXTRA_INTENT) as Intent
        val intentUris = intent.extras.getParcelableArrayList<Uri>(Intent.EXTRA_STREAM)!!
        val context = ApplicationProvider.getApplicationContext<Context>()!!

        assertThat(intent.action).isEqualTo(Intent.ACTION_SEND_MULTIPLE)
        assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_image))
        assertThat(intentUris.size).isEqualTo(selectedIndex.size)
        selectedIndex.forEachIndexed { _, testPictureIndex ->
            assertThat(intentUris.contains(testPictures[testPictureIndex].uri))
                .isTrue()
        }

        Intents.release()
    }

    @Test
    fun restoreUiState_whenRecreated() {
        // Given a resumed fragment

        // And displayed pictures
        displayTestPictures()

        // When user selects pictures
        val selectedIndex = listOf(1,2,4,7)

        selectDisplayedPictures(selectedIndex)

        // And fragment is recreated
        scenario.recreate()

        // Then fragment should restore its ui state
        testPictures.forEachIndexed { index, _ ->
            val check = if (selectedIndex.contains(index)) {
                hasDescendant(
                    allOf(
                        withId(R.id.select_item_checkBox),
                        isChecked(),
                        isDisplayed())
                )
            } else{
                hasDescendant(
                    allOf(
                        withId(R.id.select_item_checkBox),
                        isNotChecked(),
                        isDisplayed())
                )
            }

            scrollToPictureAndCheck(index,check)
        }
    }

    @Test
    fun closeSelectionUi_whenAllVideosUnSelected() {
        // Given a resumed fragment

        // And displayed pictures
        displayTestPictures()

        // When user start multi selection by long click on displayed picture item
        val selectedIndex = 0
        scrollToPictureAndPerform(selectedIndex, longClick())

        // And then un select the same item
        scrollToPictureAndPerform(selectedIndex, click())

        // Then fragment should close multi select ui
        scenario.onFragment {
            val field = PicturesFragment::class.java.getDeclaredField("actionMode")
            field.isAccessible = true
            val actionMode: ActionMode =  field.get(it) as ActionMode

            assertThat(actionMode.menu.hasVisibleItems())
                .isTrue()
        }

        testPictures.forEachIndexed { index, _ ->
            scrollToPictureAndCheck(
                index,
                hasDescendant(
                    allOf(
                        withId(R.id.selectable_foreground),
                        not(isDisplayed())
                    )
                )
            )
        }
    }

    @Test
    fun openPictureDetail_whenPictureClicked() {
        // Test case fixture
        Intents.init()

        // Given a resumed fragment

        // And displayed pictures
        displayTestPictures()

        // When user click on displayed picture
        val selectedIndex = 0
        scrollToPictureAndPerform(selectedIndex, click())

        // Then fragment should start picture activity, with clicked picture url as extra
        val context = ApplicationProvider.getApplicationContext<Context>()

        intended(hasComponent(PictureDetailActivity::class.java.name))
        intended(hasExtra(context.getString(R.string.extra_pic_uri),
            testPictures[selectedIndex].uri))

        Intents.release()
    }

    private fun selectDisplayedPictures(selectedIndex: List<Int>) {
        selectedIndex.forEachIndexed { index, testVideosIndex ->
            // If this is the first selected picture, initiate multi selection and select,
            // else just select video
            val action: ViewAction = if (index == 0) longClick() else click()
            scrollToPictureAndPerform(testVideosIndex,action)
        }
    }

    private fun scrollToPicture(position: Int) {
        // Scroll to requested pictures list position
        onView(withId(R.id.pictures_list))
            .perform(scrollToPosition<PictureViewHolder>(position))
    }

    private fun scrollToPictureAndCheck(position: Int, viewMatcher: Matcher<View>) {
        // Scroll to requested pictures list position
        scrollToPicture(position)

        // Perform requested checking on view at position
        onView(withRecyclerView(R.id.pictures_list).atPosition(position))
            .check(matches(viewMatcher))
    }

    private fun scrollToPictureAndPerform(position: Int, viewAction: ViewAction) {
        onView(withId(R.id.pictures_list))
            .perform(scrollToPosition<PictureViewHolder>(position))
            .perform(
                actionOnItemAtPosition<PictureViewHolder>(
                    position,
                    viewAction
                )
            )
        shadowOf(getMainLooper()).idle()
    }

    private fun displayTestPictures() {
        picturesLiveData.value = testPictures

        shadowOf(getMainLooper()).idle()
    }
}
