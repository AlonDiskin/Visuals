package com.diskin.alon.visuals.catalog.presentation.controller

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.diskin.alon.visuals.photos.presentation.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_picture_viewer.*
import javax.inject.Inject

class PictureViewerActivity : AppCompatActivity() {

    companion object {
        private const val NUM_PAGES = 2
        private const val PICTURE_VIEW_POS = 0
    }

    @Inject
    lateinit var fragmentsFactory: PictureViewerFragmentFactory
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject activity
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_viewer)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup view pager
        viewPager = pager
        viewPager.adapter = ScreenSlidePagerAdapter(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            val uriKey = getString(R.string.extra_pic_uri)
            val picUri = intent.extras?.getParcelable<Uri>(uriKey)!!

            return when(position) {
                // Return a fragment that shows picture full view
                PICTURE_VIEW_POS -> fragmentsFactory.createPictureViewFragment(picUri)

                // Return a fragment that shows picture detail
                else -> fragmentsFactory.createPictureDetailFragment(picUri)
            }
        }
    }
}
