package com.diskin.alon.visuals.recyclebin.featuretest

import android.net.Uri
import android.os.Looper
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.recuclebin.presentation.*
import com.diskin.alon.visuals.recuclebin.presentation.R
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedPictureBinding
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedVideoBinding
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import com.diskin.alon.visuals.recyclebin.featuretest.RecyclerViewMatcher.withRecyclerView
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import io.mockk.verify
import org.robolectric.Shadows

/**
 * Step definitions for the 'User filters displayed trashed items' scenario.
 */
class FilterTrashedSteps(
    private val testDao: TrashedItemDao,
    mockedMediaProvider: DeviceMediaProvider<MediaStoreVisual>
) : ListTrashedItemsBackgroundSteps(testDao, mockedMediaProvider) {

    @Given("^User has public media on device$")
    override fun userHasPublicMediaOnDevice(rows: List<TableRow>) {
        super.userHasPublicMediaOnDevice(rows)
    }

    @And("^User hes media items in app recycle bin$")
    override fun userHesMediaItemsInAppRecycleBin(rows: List<TableRow>) {
        super.userHesMediaItemsInAppRecycleBin(rows)
    }

    @When("^User opens recycle bin screen$")
    override fun userOpensRecycleBinScreen() {
        super.userOpensRecycleBinScreen()
    }

    @Then("^Only trashed items should be shown sorted by trashing date in desc order$")
    override fun onlyTrashedItemsShouldBeShownSortedByTrashingDateInDescOrder(rows: List<TableRow>) {
        super.onlyTrashedItemsShouldBeShownSortedByTrashingDateInDescOrder(rows)
    }

    @When("^user select \"([^\"]*)\" from filter menu$")
    fun userSelectFromFilterMenu(filter: String) {
        val testFilter = when(filter) {
            "all" -> R.id.action_filter_all
            "video" -> R.id.action_filter_video
            "image" -> R.id.action_filter_image
            else -> null
        }!!
        val filterMenuItem = ActionMenuItem(
            ApplicationProvider.getApplicationContext(),
            0,
            testFilter,
            0,
            0,
            null
        )

        scenario.onFragment { it.onOptionsItemSelected(filterMenuItem) }

        // Wait for main looper to idle
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        // TODO find a way to sync test thread(main) with AsyncDiffer from ListAdapter
        Thread.sleep(500L)
    }

    @Then("^Only trashed items of \"([^\"]*)\" type should be shown sorted bt date in desc order$")
    fun onlyTrashedItemsOfTypeShouldBeShownSortedBtDateInDescOrder(filter: String) {
        // Resolve expected items, according to test filter
        val expectedTrashedItems = testDao
            .getAll()
            .blockingFirst()
            .filter { entity ->
                when (filter) {
                    "all" -> true
                    "image" -> entity.type == TrashedEntityType.PICTURE
                    "video" -> entity.type == TrashedEntityType.VIDEO
                    else -> false
                }
            }
            .map { entity ->
                TrashedItem(
                    Uri.parse(entity.uri),
                    when (entity.type) {
                        TrashedEntityType.PICTURE -> TrashedItemType.PICTURE
                        TrashedEntityType.VIDEO -> TrashedItemType.VIDEO
                    }
                )
            }
            .reversed()

        // Verify ui display expected size of items
        scenario.onFragment {
            val rv = it.view!!.findViewById<RecyclerView>(R.id.trashedList)
            assertThat(rv.adapter!!.itemCount).isEqualTo(expectedTrashedItems.size)
        }

        // Verify displayed items are of expected type,according to test filter
        expectedTrashedItems.forEachIndexed { index, item ->
            onView(
                withRecyclerView(R.id.trashedList)
                    .atPositionOnView(index, getTrashedViewType(item))
            )
                .check(matches(isDisplayed()))

            verify {
                when(item.type) {
                    TrashedItemType.PICTURE -> loadImage(any(),item.uri)
                    TrashedItemType.VIDEO -> loadThumbnail(any(),item.uri)
                }
            }

            scenario.onFragment {
                val rv = it.view!!.findViewById<RecyclerView>(R.id.trashedList)
                val binding = DataBindingUtil.getBinding<ViewDataBinding>(
                    rv[index]
                )

                val boundedItem = if (binding is TrashedVideoBinding) {
                    binding.trashedItem!!
                } else  {
                    (binding as TrashedPictureBinding).trashedItem!!
                }

                assertThat(boundedItem.uri).isEqualTo(item.uri)
            }
        }
    }
}