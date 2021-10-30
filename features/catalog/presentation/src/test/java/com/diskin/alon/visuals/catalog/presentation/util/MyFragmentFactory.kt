package com.diskin.alon.visuals.catalog.presentation.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.diskin.alon.visuals.catalog.presentation.controller.PictureFragment

class MyFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return if (loadFragmentClass(classLoader, className) == PictureFragment::class.java) {
            PictureFragment()

        } else {
            super.instantiate(classLoader, className)
        }
    }
}