package com.diskin.alon.visuals.videos.presentation.controller

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.diskin.alon.visuals.videos.presentation.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_video_player.*
import javax.inject.Inject

class VideoPlayerActivity : AppCompatActivity() {

    @Inject
    lateinit var fragmentsFactory: VideoPlayerFragmentsFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject activity collaborators
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Create video preview fragment
        if (savedInstanceState == null) {
            val uriKey = getString(R.string.extra_vid_uri)
            val videoUri = intent.extras?.getParcelable<Uri>(uriKey)!!

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container,fragmentsFactory.createVideoPreviewFragment(videoUri))
                .commitNow()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
