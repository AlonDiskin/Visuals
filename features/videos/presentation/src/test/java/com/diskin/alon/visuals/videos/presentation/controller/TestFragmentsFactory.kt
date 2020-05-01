package com.diskin.alon.visuals.videos.presentation.controller

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class TestFragmentsFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return if (loadFragmentClass(classLoader, className) == VideoPreviewFragment::class.java) {
            VideoPreviewFragment()

        } else {
            super.instantiate(classLoader, className)
        }
    }
}