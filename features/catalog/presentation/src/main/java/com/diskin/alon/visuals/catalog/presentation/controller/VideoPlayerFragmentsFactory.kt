package com.diskin.alon.visuals.catalog.presentation.controller

import android.net.Uri
import androidx.fragment.app.Fragment

/**
 * Video player view fragments factory contract.
 */
interface VideoPlayerFragmentsFactory {

    fun createVideoPreviewFragment(videoUri: Uri): Fragment

    fun createVideoDetailFragment(vidUri: Uri): Fragment
}