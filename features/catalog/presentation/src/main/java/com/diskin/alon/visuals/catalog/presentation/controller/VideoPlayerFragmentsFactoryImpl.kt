package com.diskin.alon.visuals.catalog.presentation.controller

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.diskin.alon.visuals.catalog.presentation.viewmodel.VideoDetailViewModelImpl
import javax.inject.Inject

class VideoPlayerFragmentsFactoryImpl @Inject constructor() : VideoPlayerFragmentsFactory {

    override fun createVideoPreviewFragment(videoUri: Uri): Fragment {
        val args = Bundle().apply {
            putParcelable(VideoPreviewFragment.KEY_VID_URI,videoUri)
        }

        return VideoPreviewFragment().apply { arguments = args }
    }

    override fun createVideoDetailFragment(videoUri: Uri): Fragment {
        val args = Bundle().apply {
            putParcelable(VideoDetailViewModelImpl.VID_URI,videoUri)
        }

        return VideoDetailFragment().apply { arguments = args }
    }
}