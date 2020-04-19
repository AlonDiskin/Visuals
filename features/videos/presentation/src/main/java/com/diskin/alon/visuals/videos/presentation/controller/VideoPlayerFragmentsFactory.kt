package com.diskin.alon.visuals.videos.presentation.controller

import android.net.Uri
import androidx.fragment.app.Fragment

/**
 * Video player view fragments factory contract.
 */
interface VideoPlayerFragmentsFactory {

    fun createVideoPreviewFragment(videoUri: Uri): Fragment
}