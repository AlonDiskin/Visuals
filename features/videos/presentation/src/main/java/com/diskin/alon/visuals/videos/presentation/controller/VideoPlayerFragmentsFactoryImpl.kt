package com.diskin.alon.visuals.videos.presentation.controller

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import javax.inject.Inject

class VideoPlayerFragmentsFactoryImpl @Inject constructor() : VideoPlayerFragmentsFactory {

    override fun createVideoPreviewFragment(videoUri: Uri): Fragment {
        val args = Bundle().apply {
            putParcelable(VideoPreviewFragment.KEY_VID_URI,videoUri)
        }

        return VideoPreviewFragment().apply { arguments = args }
    }
}