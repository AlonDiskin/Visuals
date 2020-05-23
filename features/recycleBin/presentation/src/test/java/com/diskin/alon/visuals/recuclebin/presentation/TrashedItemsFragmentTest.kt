package com.diskin.alon.visuals.recuclebin.presentation

import android.net.Uri
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
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

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(
            TrashedItemsFragment::class.java,
            null,
            R.style.AppTheme,
            null)
    }

    @Test
    fun displayUserTrashedItemsState_whenResumed() {
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
}