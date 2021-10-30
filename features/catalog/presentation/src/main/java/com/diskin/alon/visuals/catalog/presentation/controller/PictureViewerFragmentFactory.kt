package com.diskin.alon.visuals.catalog.presentation.controller

import android.net.Uri
import androidx.fragment.app.Fragment

/**
 * Factory contract for building fragments for the picture detail activity.
 */
interface PictureViewerFragmentFactory {

    fun createPictureViewFragment(picUri: Uri): Fragment

    fun createPictureDetailFragment(picUri: Uri): Fragment
}