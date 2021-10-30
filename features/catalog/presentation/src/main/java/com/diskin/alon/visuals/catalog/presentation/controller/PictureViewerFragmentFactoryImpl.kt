package com.diskin.alon.visuals.catalog.presentation.controller

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PictureDetailViewModelImpl
import javax.inject.Inject

class PictureViewerFragmentFactoryImpl @Inject constructor() :  PictureViewerFragmentFactory {

    override fun createPictureViewFragment(picUri: Uri): Fragment {
        val fragmentArgs = Bundle().apply {
            putParcelable(PictureFragment.KEY_PIC_URI,picUri)
        }

        return PictureFragment().apply { arguments = fragmentArgs }
    }

    override fun createPictureDetailFragment(picUri: Uri): Fragment {
        val fragmentArgs = Bundle().apply {
            putParcelable(PictureDetailViewModelImpl.KEY_PIC_URI,picUri)
        }

        return PictureDetailFragment().apply { arguments = fragmentArgs }
    }
}