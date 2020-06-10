package com.diskin.alon.visuals.recyclebin.featuretest.restoretrashed

import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.visuals.recuclebin.presentation.R
import com.diskin.alon.visuals.recyclebin.data.MediaStoreVisual
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import gherkin.ast.TableRow
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowAlertDialog

/**
 * Step definitions for the 'User restores all trash items' scenario.
 */
class RestoreAllSteps(
    private val testDao: TrashedItemDao,
    mockedMediaProvider: DeviceMediaProvider<MediaStoreVisual>
) : RestoreTrashedStepsBackground(testDao, mockedMediaProvider) {

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

    @When("^User selects to restore all shown items$")
    fun userSelectsToRestoreAllShownItems() {
        // Open options menu
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        // Select 'restore all' menu item
        onView(withText(R.string.action_restore_all))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        // Approve dialog
        (ShadowAlertDialog.getLatestDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).performClick()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^All all restored items should be removed form app recycle bin storage$")
    fun allAllRestoredItemsShouldBeRemovedFormAppRecycleBinStorage() {
        // Verify all trash items removed from storage
        assertThat(testDao.getAll().blockingFirst()).isEmpty()
    }

    @And("^Recycle bin browser screen should remove all displayed items$")
    fun recycleBinBrowserScreenShouldRemoveAllDisplayedItems() {
        // Verify size of displayed trash items list is zero
        scenario.onFragment {
            val rv = it.view!!.findViewById<RecyclerView>(R.id.trashList)
            assertThat(rv.adapter!!.itemCount).isEqualTo(0)
        }
    }
}